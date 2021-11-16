/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;

/**
 *
 * @author PP42
 */
@Remote
public interface ReservationSessionBeanRemote {

    public void walkInReserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity) throws DoesNotExistException, BeanValidationException;

    public void reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity, String username) throws DoesNotExistException, BeanValidationException;

    public ReservationEntity retrieveReservationById(Long resId) throws DoesNotExistException;

    public Set<ReservationEntity> retrieveReservationByCheckInAndGuest(LocalDate checkIn, String username) throws DoesNotExistException;

    public List<ReservationEntity> viewExceptionReport(LocalDate reportDate);

    public void checkOut(Long resId) throws DoesNotExistException;

}
