/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GuestEntity;
import entity.ReservationEntity;
import java.util.Set;
import javax.ejb.Remote;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Remote
public interface GuestSessionBeanRemote {

    public GuestEntity guestLogin(String username, String password) throws InvalidLoginException;

    public GuestEntity createNewGuest(GuestEntity newGuestEntity) throws AlreadyExistsException, UnknownPersistenceException, BeanValidationException;

    public GuestEntity retrieveGuestByUsername(String username) throws DoesNotExistException;

    public Set<ReservationEntity> retrieveAllReservationsByGuest(String emailAddress) throws DoesNotExistException;

    public ReservationEntity retrieveReservationsByGuest(String emailAddress, Long reservationId) throws DoesNotExistException;

}
