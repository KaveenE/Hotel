/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.PartnerEntity;
import entity.PartnerReservationEntity;
import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.helper.BossHelper;
import util.helper.Pair;

/**
 *
 * @author PP42
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @WebMethod(operationName = "searchRoomTypeReservableQuantityForPartner")
    public List<Pair<String, Integer>> searchRoomTypeReservableQuantityForPartner(@WebParam(name = "checkIn") Date checkIn,
            @WebParam(name = "checkOut") Date checkOut)
            throws DoesNotExistException {
        return roomTypeSessionBean.searchRoomTypeReservableQuantityForPartner(BossHelper.dateToLocalDate(checkIn), BossHelper.dateToLocalDate(checkOut));
    }

    @WebMethod(operationName = "reserveRoomsByRoomType")
    public void reserveRoomsByRoomType(@WebParam(name = "checkIn") Date checkIn, @WebParam(name = "checkOut") Date checkOut,
            @WebParam(name = "roomType") String roomTypeName, @WebParam(name = "roomQuality") Long roomQuantity,
            @WebParam(name = "username") String username) throws DoesNotExistException, BeanValidationException {
        ReservationEntity reservationEntity = new PartnerReservationEntity(checkIn, checkOut);
        reservationSessionBean.reserveRoomsByRoomType(reservationEntity, roomTypeName, roomQuantity, username);
    }

    @WebMethod(operationName = "partnerLogin")
    public PartnerEntity partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws InvalidLoginException {
        return partnerSessionBean.partnerLogin(username, password);
    }

    @WebMethod(operationName = "retrieveAllReservationsByPartner")
    public Set<ReservationEntity> retrieveAllReservationsByPartner(@WebParam(name = "emailAddress") String emailAddress) throws DoesNotExistException {
        return partnerSessionBean.retrieveAllReservationsByPartner(emailAddress);
    }
    
    @WebMethod(operationName = "retrieveReservationsByPartner")
    public ReservationEntity retrieveReservationsByPartner(@WebParam(name = "username") String username, @WebParam(name = "reservationId")Long reservationId) throws DoesNotExistException {
        return partnerSessionBean.retrieveReservationsByPartner(username, reservationId);
    }
}
