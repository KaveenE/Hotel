/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import java.time.LocalDate;
import java.util.Map;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.DoesNotExistException;

/**
 *
 * @author seanc
 */
@WebService(serviceName = "RoomTypeWebService")
@Stateless()
public class RoomTypeWebService {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @WebMethod(operationName = "searchRoomTypeReservableQuantity")
    public Map<String, Integer> searchRoomTypeReservableQuantity(@WebParam(name = "checkIn") LocalDate checkIn, @WebParam(name = "checkOut") LocalDate checkOut)
            throws DoesNotExistException {
        return roomTypeSessionBean.searchRoomTypeReservableQuantity(checkIn, checkOut);
    }
}
