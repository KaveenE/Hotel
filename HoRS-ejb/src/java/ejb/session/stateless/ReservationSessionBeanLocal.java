/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author enkav
 */
@Local
public interface ReservationSessionBeanLocal {

    public List<ReservationEntity> retrieveReservationByCheckIn(Date checkIn);
    
}
