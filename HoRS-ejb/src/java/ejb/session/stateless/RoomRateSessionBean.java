/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PeakRateEntity;
import entity.PromoRateEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.RoomRateDoesNotExistException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public List<RoomRateAbsEntity> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRateAbsEntity rr");
        List<RoomRateAbsEntity> roomRates = query.getResultList();

        for (RoomRateAbsEntity rr : roomRates) {
            rr.getRoomTypeEntity();
        }

        return roomRates;
    }

    @Override
    public RoomRateAbsEntity retrieveRoomRateById(Long roomRateId) throws DoesNotExistException {
        RoomRateAbsEntity roomRate = em.find(RoomRateAbsEntity.class, roomRateId);
        
        roomRate = BossHelper.requireNonNull(roomRate, new RoomRateDoesNotExistException());
        if(roomRate.getIsDisabled()) {
            throw new RoomRateDoesNotExistException();
        }
 
        roomRate.getRoomTypeEntity();

        return roomRate;
    }

    @Override
    public RoomRateAbsEntity createRoomRateWithExistingRoomType(RoomRateAbsEntity roomRate, Long roomTypeId) throws DoesNotExistException , BeanValidationException{
        RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        roomType.associateRoomRateAbsEntity(roomRate);
        BossHelper.throwValidationErrorsIfAny(roomRate);
        
        em.persist(roomRate);
        em.flush();

        return roomRate;
    }

    @Override
    public void updateRoomRate(RoomRateAbsEntity roomRate) throws DoesNotExistException, BeanValidationException{
        
        BossHelper.requireNonNull(roomRate, new RoomRateDoesNotExistException());
        BossHelper.requireNonNull(roomRate.getRoomRateId(), new RoomRateDoesNotExistException());
        BossHelper.throwValidationErrorsIfAny(roomRate);

        RoomRateAbsEntity roomRateToUpdate = retrieveRoomRateById(roomRate.getRoomRateId());
        roomRateToUpdate.setName(roomRate.getName());
        roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());

        if (roomRate instanceof PromoRateEntity) {
            PromoRateEntity promoRoomRateToUpdate = (PromoRateEntity) roomRateToUpdate;
            promoRoomRateToUpdate.setValidFrom(((PromoRateEntity) roomRate).getValidFrom());
            promoRoomRateToUpdate.setValidTo(((PromoRateEntity) roomRate).getValidTo());
        }

        if (roomRate instanceof PeakRateEntity) {
            PeakRateEntity peakRoomRateToUpdate = (PeakRateEntity) roomRateToUpdate;
            peakRoomRateToUpdate.setValidFrom(((PeakRateEntity) roomRate).getValidFrom());
            peakRoomRateToUpdate.setValidTo(((PeakRateEntity) roomRate).getValidTo());
        }

    }

    @Override
    public void deleteRoomRateById(Long id) throws DoesNotExistException {
        //Delete a particular room rate record.
        //A room rate record can only be deleted if it is not used. (ie 0 RLE?)
        //Otherwise, it should be marked as disabled and new reservation should not be made with the disabled room rate.

        RoomRateAbsEntity potentialRoomRateToDelete = retrieveRoomRateById(id);

        if (!potentialRoomRateToDelete.getIsDisabled() && potentialRoomRateToDelete.getReservations().isEmpty()) {
            potentialRoomRateToDelete.getRoomTypeEntity().disassociateRoomRateAbsEntity(potentialRoomRateToDelete);
            em.remove(potentialRoomRateToDelete);

        } else if (!potentialRoomRateToDelete.getIsDisabled() && !potentialRoomRateToDelete.getReservations().isEmpty()) {
            potentialRoomRateToDelete.setIsDisabled(true);
        } else {
            throw new RoomRateDoesNotExistException("Room rate is disabled!");
        }
    }

    @Override
    public List<RoomRateAbsEntity> retrieveRoomRatesBySubclass(Class subclass) throws DoesNotExistException {
        return em.createQuery("SELECT rr FROM RoomRateAbsEntity rr WHERE TYPE(rr) = :subclass")
                .setParameter("subclass", subclass)
                .getResultList();
    }

}
