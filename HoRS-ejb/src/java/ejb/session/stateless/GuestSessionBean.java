/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GuestEntity;
import entity.ReservationEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.GuestAlreadyExistsException;
import util.exception.GuestDoesNotExistException;
import util.exception.ReservationDoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public GuestEntity createNewGuest(GuestEntity newGuestEntity) throws AlreadyExistsException, UnknownPersistenceException, BeanValidationException {
        BossHelper.throwValidationErrorsIfAny(newGuestEntity);
        try {
            em.persist(newGuestEntity);
            em.flush();

        } catch (PersistenceException ex) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(ex, new GuestAlreadyExistsException());
        }
        return newGuestEntity;
    }

    @Override
    public List<GuestEntity> retrieveAllGuests() {
        Query query = em.createQuery("SELECT p FROM GuestEntity p");
        return query.getResultList();
    }

    @Override
    public GuestEntity retrieveGuestByUsername(String emailAddress) throws DoesNotExistException {
        try {
            return (GuestEntity) em.createQuery("SELECT g FROM GuestEntity g WHERE g.emailAddress = :emailAddress")
                    .setParameter("emailAddress", emailAddress)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestDoesNotExistException("Guest Username " + emailAddress + " does not exist!");
        }
    }
    
    //retrieves all reservation records for the guest
    @Override
    public Set<ReservationEntity> retrieveAllReservationsByGuest(String emailAddress) throws DoesNotExistException {
        return retrieveGuestByUsername(emailAddress).getGuestReservationEntities()
                                                    .stream().map(guestRes -> (ReservationEntity)guestRes)
                                                    .collect(Collectors.toSet());
    }
    
    //retrieves a particular reservation record
    @Override
    public ReservationEntity retrieveReservationsByGuest(String emailAddress, Long reservationId) throws DoesNotExistException {
        return retrieveGuestByUsername(emailAddress).getGuestReservationEntities()
                                                    .stream()
                                                    .map(guestRes -> (ReservationEntity)guestRes)
                                                    .filter(res -> res.getReservationId().equals(reservationId))
                                                    .findFirst()
                                                    .orElseThrow(() -> new ReservationDoesNotExistException());
    }

    @Override
    public GuestEntity guestLogin(String emailAddress, String password) throws InvalidLoginException {
        try {
            GuestEntity guestEntity = retrieveGuestByUsername(emailAddress);

            if (guestEntity.getPassword().equals(password)) {
                return guestEntity;
            } else {
                throw new InvalidLoginException("Username does not exist or invalid password!");
            }
        } catch (DoesNotExistException ex) {
            throw new InvalidLoginException("Username does not exist or invalid password!");
        }
    }

}
