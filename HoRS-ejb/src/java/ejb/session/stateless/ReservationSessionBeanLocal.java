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
import javax.ejb.Local;
import util.exception.DoesNotExistException;

/**
 *
 * @author enkav
 */
@Local
public interface ReservationSessionBeanLocal {

    public Set<ReservationEntity> retrieveReservationByCheckIn(LocalDate checkIn);

    public void reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity, String username) throws DoesNotExistException;

}
