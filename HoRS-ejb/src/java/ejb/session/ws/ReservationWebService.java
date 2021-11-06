/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.ReservationEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.DoesNotExistException;

/**
 *
 * @author PP42
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @WebMethod(operationName = "reserveRoomsByRoomType")
    public void reserveRoomsByRoomType(@WebParam(name = "reservation") ReservationEntity reservation, @WebParam(name = "roomType") String roomTypeName,
                                        @WebParam(name = "roomQuality") Long roomQuantity, @WebParam(name = "username") String username) throws DoesNotExistException {
        reservationSessionBean.reserveRoomsByRoomType(reservation, roomTypeName, roomQuantity, username);
    }
}
