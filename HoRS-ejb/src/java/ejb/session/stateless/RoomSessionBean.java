/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomTypeEntity;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.RoomAlreadyExistsException;
import util.exception.RoomDoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public RoomEntity createRoomWithExistingRoomType(RoomEntity roomEntity, Long roomTypeId) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException {
        RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        roomType.associateRoomEntities(roomEntity);

        try {
            em.persist(roomEntity);
            em.flush();
        } catch (PersistenceException persistenceEx) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(persistenceEx, new RoomAlreadyExistsException());
        }
        return roomEntity;
    }

    @Override
    public RoomEntity retrieveRoomById(Long roomId) throws DoesNotExistException {
        RoomEntity room = em.find(RoomEntity.class, roomId);
        return BossHelper.requireNonNull(room, new RoomDoesNotExistException());
    }

    @Override
    public List<RoomEntity> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM RoomEntity r");
        List<RoomEntity> roomEntities = query.getResultList();

        for (RoomEntity re : roomEntities) {
            re.getRoomTypeEntity();
        }

        return roomEntities;
    }

    @Override
    public void updateRoom(RoomEntity room) throws DoesNotExistException, AlreadyExistsException {
        BossHelper.requireNonNull(room, new RoomDoesNotExistException());
        BossHelper.requireNonNull(room.getRoomId(), new RoomDoesNotExistException());
        
        RoomEntity roomToUpdate = retrieveRoomById(room.getRoomId());
        
        if(!Objects.equals(roomToUpdate.getFloorUnitNo(), roomToUpdate.getFloorUnitNo())  && uniqueFieldAlreadyExists(room.getFloorUnitNo())) {
            throw new RoomAlreadyExistsException("This unique floor number already exists!");
        }

        roomToUpdate.setFloorUnitNo(room.getFloorUnitNo());
        roomToUpdate.setRoomStatusEnum(room.getRoomStatusEnum());
    }

    private boolean uniqueFieldAlreadyExists(Long floorUnitNo) {
        return !em.createQuery("SELECT re FROM RoomEntity re WHERE re.floorUnitNo = :floorUnitNo")
                .setParameter("floorUnitNo", floorUnitNo)
                .getResultList()
                .isEmpty();
    }

    //Placeholder for "Delete Room" (UC 14)
    @Override
    public void deleteRoomByFloorUnitNo(Long floorUnitNo) throws DoesNotExistException {
        //Delete a particular room record.
        //A room record can only be deleted if it is not used (ie 0 RLE?)
        //Otherwise, it should be marked as disabled, excluded from the hotel room inventory for that particular room type and should not be allocated to a new reservation.
        RoomEntity potentialRoomEntityToDelete = retrieveRoomByFloorUnitNo(floorUnitNo);

        if (!potentialRoomEntityToDelete.getIsDisabled() && potentialRoomEntityToDelete.getReservationEntities().isEmpty()) {
            potentialRoomEntityToDelete.getRoomTypeEntity().disassociateRoomEntities(potentialRoomEntityToDelete);
            em.remove(potentialRoomEntityToDelete);

        } else if (!potentialRoomEntityToDelete.getIsDisabled() && !potentialRoomEntityToDelete.getReservationEntities().isEmpty()) {
            potentialRoomEntityToDelete.setIsDisabled(true);
        } else {
            throw new RoomDoesNotExistException("The room you want to delete is disabled!");
        }

    }

    @Override
    public RoomEntity retrieveRoomByFloorUnitNo(Long floorUnitNo) throws DoesNotExistException {
        try {
            return (RoomEntity) em.createQuery("SELECT re FROM RoomEntity re WHERE re.floorUnitNo = :floorUnitNo")
                    .setParameter("floorUnitNo", floorUnitNo)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomDoesNotExistException();
        }
    }

}
