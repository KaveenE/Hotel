/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DoesNotExistException;

/**
 *
 * @author enkav
 */
@Remote
public interface ReservationSessionBeanRemote {

    public void walkInReserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity) throws DoesNotExistException;

    public void reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity, String username) throws DoesNotExistException;

    public ReservationEntity retrieveReservationById(Long resId) throws DoesNotExistException;

    public List<ReservationEntity> retrieveReservationByCheckInAndGuest(LocalDate checkIn, String username) throws DoesNotExistException;

}
