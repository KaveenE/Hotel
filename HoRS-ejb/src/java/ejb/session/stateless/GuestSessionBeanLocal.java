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
import javax.ejb.Local;
import util.exception.DoesNotExistException;

/**
 *
 * @author SCXY
 */
@Local
public interface GuestSessionBeanLocal {

    public List<GuestEntity> retrieveAllGuests();

    public Set<ReservationEntity> retrieveAllReservationsByGuest(String emailAddress) throws DoesNotExistException;
    public GuestEntity retrieveGuestByUsername(String emailAddress) throws DoesNotExistException;
    
}
