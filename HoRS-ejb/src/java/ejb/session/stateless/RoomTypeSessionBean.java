/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.RoomStatusEnum;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.RoomRateDoesNotExistException;
import util.exception.RoomTypeAlreadyExistsException;
import util.exception.RoomTypeDoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public RoomTypeEntity retrieveRoomTypeById(Long roomTypeId) throws DoesNotExistException {
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        return BossHelper.requireNonNull(roomType, new RoomTypeDoesNotExistException());
    }

    @Override
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomTypeEntity rt");
        List<RoomTypeEntity> allRoomTypes = query.getResultList();
        allRoomTypes.stream().forEach(rt -> rt.getAmenities().size());

        return allRoomTypes;
    }

    @Override
    public void updateRoomType(RoomTypeEntity roomType) throws DoesNotExistException, AlreadyExistsException {
        BossHelper.requireNonNull(roomType, new RoomTypeDoesNotExistException());
        BossHelper.requireNonNull(roomType.getRoomTypeId(), new RoomTypeDoesNotExistException());

        RoomTypeEntity roomTypeToUpdate = retrieveRoomTypeById(roomType.getRoomTypeId());
        if(!roomTypeToUpdate.getName().equals(roomType.getName()) && uniqueFieldAlreadyExists(roomType.getName())) {
            throw new RoomTypeAlreadyExistsException("This unique name already exists!");
        }
        
        roomTypeToUpdate.setName(roomType.getName());
        roomTypeToUpdate.setAmenities(roomType.getAmenities());
        roomTypeToUpdate.setBed(roomType.getBed());
        roomTypeToUpdate.setCapacity(roomType.getCapacity());
        roomTypeToUpdate.setDescription(roomType.getDescription());
        roomTypeToUpdate.setMySize(roomType.getMySize());
        roomTypeToUpdate.setRanking(roomType.getRanking());

    }

    private boolean uniqueFieldAlreadyExists(String name) {
        return !em.createQuery("SELECT rt FROM RoomTypeEntity rt WHERE rt.roomConfig.name = :name")
                .setParameter("name", name)
                .getResultList()
                .isEmpty();
    }

    @Override
    public RoomTypeEntity createRoomType(RoomTypeEntity roomType) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException {
        try {
            em.persist(roomType);
            em.flush();
        } catch (PersistenceException persistenceExceptionThrown) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(persistenceExceptionThrown, new RoomTypeAlreadyExistsException());
        }

        return roomType;
    }

    @Override
    public void deleteRoomTypeByName(String name) throws DoesNotExistException {
        //Delete a particular room type record.
        //A room type record can only be deleted if it is not used. 
        //Otherwise, it should be marked as disabled and no new room should be created for disabled room type. 
        RoomTypeEntity potentialRoomTypeToDelete = retrieveRoomTypeByName(name);

        if (!potentialRoomTypeToDelete.getIsDisabled() && potentialRoomTypeToDelete.getRoomEntities().isEmpty()) {
            em.remove(potentialRoomTypeToDelete);
        } else if (!potentialRoomTypeToDelete.getIsDisabled() && !potentialRoomTypeToDelete.getRoomEntities().isEmpty()) {
            potentialRoomTypeToDelete.setIsDisabled(true);
        } else {
            throw new RoomTypeDoesNotExistException("The room type you want to delete is disabled!");
        }
    }

    @Override
    public RoomTypeEntity retrieveRoomTypeByName(String name) throws DoesNotExistException {
        try {
            RoomTypeEntity rt = (RoomTypeEntity) em.createQuery("SELECT rt FROM RoomTypeEntity rt WHERE rt.roomConfig.name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            rt.getAmenities().size();

            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeDoesNotExistException();
        }
    }

    @Override
    public RoomRateAbsEntity retrieveRoomRateFromRoomType(String roomTypeName, Class subclass) throws DoesNotExistException {
        try {
            RoomRateAbsEntity rr = (RoomRateAbsEntity) em.createQuery("SELECT rt"
                    + " FROM RoomTypeEntity rt JOIN rt.roomRateAbsEntities AS rr"
                    + " WHERE TYPE(rr) = :subclass AND rt.roomConfig.name = :roomTypeName")
                    .setParameter("subclass", subclass)
                    .setParameter("roomTypeName", roomTypeName)
                    .getSingleResult();

            return rr;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateDoesNotExistException(subclass.getName() + " for this room type does not exist");
        }
    }

    @Override
    public Map<String, Integer> walkInSearchRoomTypeReservableQuantity(LocalDate checkOut) throws DoesNotExistException {
        return searchRoomTypeReservableQuantity(LocalDate.now(), checkOut);
    }

    //Gives you the mapping of the room type and the respective allocatable quantity
    @Override
    public Map<String, Integer> searchRoomTypeReservableQuantity(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException {
        Map<String, Integer> roomTypeToAvailableRooms = new HashMap<>();

        int freeRooms;
        for (RoomTypeEntity potentialRoomTypeToReserve : retrieveAllRoomTypes()) {
            if (potentialRoomTypeToReserve.getIsDisabled()) {
                break;
            }
            freeRooms = getAllocatableQuantityByRoomType(checkIn, checkOut, potentialRoomTypeToReserve.getName());
            roomTypeToAvailableRooms.put(potentialRoomTypeToReserve.getName(), freeRooms);
        }

        return roomTypeToAvailableRooms;
    }

    //Gives you the allocatable quantity of the room type 
    private Integer getAllocatableQuantityByRoomType(LocalDate checkIn, LocalDate checkOut, String roomTypeName) throws DoesNotExistException {
        RoomTypeEntity selectedRoomType = this.retrieveRoomTypeByName(roomTypeName);

        Set<RoomEntity> availableAndEnabledRooms = selectedRoomType.getRoomEntities()
                .stream()
                .filter(room -> !room.getIsDisabled() && room.getRoomStatusEnum() == RoomStatusEnum.AVAILABLE)
                .collect(Collectors.toSet());

        boolean free;
        int roomsFree = 0;
        for (RoomEntity potentialFreeRoom : availableAndEnabledRooms) {

            //Room is not free if any of its RLE coincides with guest's period of stay
            free = potentialFreeRoom.getReservationEntities()
                    .stream()
                    .allMatch(rle -> isBeforeInclusive(rle.getCheckOutDate(), checkIn) && isAfterInclusive(rle.getCheckInDate(), checkOut));

            if (free) {
                roomsFree++;
            }
        }

        return roomsFree;
    }

    private boolean isBeforeInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) <= 0;
    }

    private boolean isAfterInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) >= 0;
    }

}
