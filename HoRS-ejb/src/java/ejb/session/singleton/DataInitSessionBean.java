/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.EmployeeEntity;
import entity.NormalRateEntity;
import entity.PartnerEntity;
import entity.PublishedRateEntity;
import entity.RoomConfig;
import entity.RoomEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoleEnum;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Singleton
@LocalBean
//@Startup
public class DataInitSessionBean {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if(em.find(EmployeeEntity.class, 1L) == null) {
            try {
                initializeData();
            } catch (AlreadyExistsException | DoesNotExistException | UnknownPersistenceException chainedEx) {
                System.out.println(chainedEx.getMessage());
            }
        }
    }

    private void initializeData() throws AlreadyExistsException, UnknownPersistenceException, DoesNotExistException {
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.SYSTEM_OPERATOR, "systems", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.OPERATION_MANAGER, "operation", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.SALES_MANAGER, "sales", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.GUEST_RELATION_OFFICER, "guest", "password"));
        partnerSessionBean.createNewPartner(new PartnerEntity("partner", "password"));
        

        List<String> amenities = new ArrayList<>();
        amenities.add("Wifi");
        amenities.add("Order-in");
        RoomRateAbsEntity normalRateDeluxe = new NormalRateEntity("Singleton Normal", BigDecimal.valueOf(10));
        RoomRateAbsEntity publishedRateDeluxe = new PublishedRateEntity("Singleton Published", BigDecimal.valueOf(100));

        RoomTypeEntity deluxe = new RoomTypeEntity(new RoomConfig(5, "Deluxe", "Cheap shit", 20L, "1 kiddie", 1), amenities);
        deluxe = roomTypeSessionBean.createRoomType(deluxe);
        roomRateSessionBean.createRoomRateWithExistingRoomType(normalRateDeluxe, deluxe.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(publishedRateDeluxe, deluxe.getRoomTypeId());

        amenities.add("Bar");
        amenities.add("Extra service (.)(.)");
        RoomRateAbsEntity normalRateGrandSuite = new NormalRateEntity("Singleton Normal", BigDecimal.valueOf(1000));
        RoomRateAbsEntity publishedRateGrandSuite = new PublishedRateEntity("Singleton Published", BigDecimal.valueOf(10000));
        RoomTypeEntity grand = new RoomTypeEntity(new RoomConfig(1, "Grand Suite", "Super Duper Luxurious", 69L, "3 king", 5), amenities);
        grand = roomTypeSessionBean.createRoomType(grand);
        roomRateSessionBean.createRoomRateWithExistingRoomType(normalRateGrandSuite, grand.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(publishedRateGrandSuite, grand.getRoomTypeId());

        RoomEntity singletonDeluxeRoom = new RoomEntity("2102");
        RoomEntity singletonGrandRoom = new RoomEntity("2103");

        roomSessionBean.createRoomWithExistingRoomType(singletonDeluxeRoom, deluxe.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(singletonGrandRoom, grand.getRoomTypeId());

//        Date currDateTime = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(currDateTime);
//        Date checkin = c.getTime();
//        c.add(Calendar.DATE, 3);
//        Date checkout = c.getTime();

//        ReservationEntity rle1 = new ReservationEntity(checkin, checkout, );
//        ReservationEntity rle2 = new ReservationEntity(checkin, checkout, );

//        singletonGrandRoom.associateReservationLineItemEntities(rle1);
//        singletonDeluxeRoom.associateReservationLineItemEntities(rle2);
//
//        normalRateDeluxe.associateReservationLineItemEntities(rle1);
//        normalPublished.associateReservationLineItemEntities(rle2);
//        
//        em.persist(rle1);
//        em.persist(rle2);
        
//        createReservationLineItemEntity(rle1, singletonGrandRoom, normalRateDeluxe);
//        createReservationLineItemEntity(rle2, singletonDeluxeRoom, publishedRateDeluxe);
//
//        ReservationEntity1 reservation = new ReservationEntity1(new Date());
//
//        reservation.associateReservationLineItemEntities(rle1, rle2);
//
//        createReservation(reservation);

    }

//    //creating the reservation manually
//    private ReservationEntity1 createReservation(ReservationEntity1 reservationEntity) {
//        em.persist(reservationEntity);
//        em.flush();
//        return reservationEntity;
//    }
//    
//    private ReservationEntity createReservationLineItemEntity(ReservationEntity reservationLineItemEntity, RoomEntity roomEntity, RoomRateAbsEntity roomRateAbsEntity) {
//        roomEntity.associateReservationLineItemEntities(reservationLineItemEntity);
//        roomRateAbsEntity.associateReservationLineItemEntities(reservationLineItemEntity);
//        
//        em.persist(reservationLineItemEntity);
////        em.flush();
//        return reservationLineItemEntity;
//    }

}
