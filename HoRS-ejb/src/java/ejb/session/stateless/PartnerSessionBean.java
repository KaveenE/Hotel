/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import entity.ReservationEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.PartnerAlreadyExistsException;
import util.exception.PartnerDoesNotExistException;
import util.exception.ReservationDoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public PartnerEntity createNewPartner(PartnerEntity newPartnerEntity) throws AlreadyExistsException, UnknownPersistenceException {
        try {
            em.persist(newPartnerEntity);
            em.flush();

        } catch (PersistenceException ex) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(ex, new PartnerAlreadyExistsException());
        }
        return newPartnerEntity;
    }

    @Override
    public List<PartnerEntity> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p FROM PartnerEntity p");
        return query.getResultList();
    }

    @Override
    public PartnerEntity retrievePartnerByUsername(String username) throws DoesNotExistException {
        try {
            return (PartnerEntity) em.createQuery("SELECT p FROM PartnerEntity p WHERE p.username = :username")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerDoesNotExistException("Partner Username " + username + " does not exist!");
        }
    }
    
    @Override
    public List<ReservationEntity> retrieveAllReservationsByPartner(String emailAddress) throws DoesNotExistException {
        return retrievePartnerByUsername(emailAddress).getPartnerReservationEntities()
                                                    .stream().map(partnerRes -> (ReservationEntity)partnerRes)
                                                    .collect(Collectors.toList());
    }
    
    @Override
    public ReservationEntity retrieveReservationsByPartner(String emailAddress,Long reservationId) throws DoesNotExistException {
        return retrievePartnerByUsername(emailAddress).getPartnerReservationEntities()
                                                    .stream()
                                                    .map(partnerRes -> (ReservationEntity)partnerRes)
                                                    .filter(partnerRes -> partnerRes.getReservationId().equals(reservationId))
                                                    .findFirst()
                                                    .orElseThrow(() -> new ReservationDoesNotExistException());
    }
    @Override
    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginException {
        try {
            PartnerEntity partnerEntity = retrievePartnerByUsername(username);

            if (partnerEntity.getPassword().equals(password)) {
                return partnerEntity;
            } else {
                throw new InvalidLoginException("Username does not exist or invalid password!");
            }
        } catch (DoesNotExistException ex) {
            throw new InvalidLoginException("Username does not exist or invalid password!");
        }
    }

}
