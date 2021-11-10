/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.EmployeeEntity;
import entity.NormalRateEntity;
import entity.PublishedRateEntity;
import entity.RoomConfig;
import entity.RoomEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Singleton
@LocalBean
@Startup
public class TestDataSessionBean {

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

    public TestDataSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if (em.find(EmployeeEntity.class, 1L) == null) {
            try {
                initializeData();
            } catch (AlreadyExistsException | DoesNotExistException | UnknownPersistenceException | BeanValidationException chainedEx) {
                System.out.println(chainedEx.getMessage());
            }
        }
    }

    public void initializeData() throws AlreadyExistsException, UnknownPersistenceException, DoesNotExistException, BeanValidationException {
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.SYSTEM_OPERATOR, "sysadmin", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.OPERATION_MANAGER, "opmanager", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.SALES_MANAGER, "salesmanager", "password"));
        employeeSessionBean.createNewEmployee(new EmployeeEntity(EmployeeRoleEnum.GUEST_RELATION_OFFICER, "guestrelo", "password"));

        List<String> amenities = new ArrayList<>();
        amenities.add("Wi-Fi");
        RoomTypeEntity deluxeRoom = roomTypeSessionBean.createRoomType(new RoomTypeEntity(new RoomConfig(1, "Deluxe Room", "Deluxe Room", 100L, "1", 2), amenities));
        RoomTypeEntity premierRoom = roomTypeSessionBean.createRoomType(new RoomTypeEntity(new RoomConfig(2, "Premier Room", "Premier Room", 150L, "1", 2), amenities));
        amenities.add("Breakfast");
        RoomTypeEntity familyRoom = roomTypeSessionBean.createRoomType(new RoomTypeEntity(new RoomConfig(3, "Family Room", "Family Room", 250L, "3", 5), amenities));
        amenities.add("Bar");
        RoomTypeEntity juniorSuite = roomTypeSessionBean.createRoomType(new RoomTypeEntity(new RoomConfig(4, "Junior Suite", "Junior Suite", 250L, "2", 5), amenities));
        amenities.add("Dinner");
        RoomTypeEntity grandSuite = roomTypeSessionBean.createRoomType(new RoomTypeEntity(new RoomConfig(5, "Grand Suite", "Grand Suite", 500L, "5", 5), amenities));
        em.flush();

        roomRateSessionBean.createRoomRateWithExistingRoomType(new PublishedRateEntity("Deluxe Room Published", BigDecimal.valueOf(100)), deluxeRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new NormalRateEntity("Deluxe Room Normal", BigDecimal.valueOf(50)), deluxeRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new PublishedRateEntity("Premier Room Published", BigDecimal.valueOf(200)), premierRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new NormalRateEntity("Premier Room Normal", BigDecimal.valueOf(100)), premierRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new PublishedRateEntity("Family Room Published", BigDecimal.valueOf(300)), familyRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new NormalRateEntity("Family Room Normal", BigDecimal.valueOf(150)), familyRoom.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new PublishedRateEntity("Junior Suite Published", BigDecimal.valueOf(400)), juniorSuite.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new NormalRateEntity("Junior Suite Normal", BigDecimal.valueOf(200)), juniorSuite.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new PublishedRateEntity("Grand Suite Published", BigDecimal.valueOf(500)), grandSuite.getRoomTypeId());
        roomRateSessionBean.createRoomRateWithExistingRoomType(new NormalRateEntity("Grand Suite Normal", BigDecimal.valueOf(250)), grandSuite.getRoomTypeId());

        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0101"), deluxeRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0201"), deluxeRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0301"), deluxeRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0401"), deluxeRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0501"), deluxeRoom.getRoomTypeId());

        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0102"), premierRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0202"), premierRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0302"), premierRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0402"), premierRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0502"), premierRoom.getRoomTypeId());

        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0103"), familyRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0203"), familyRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0303"), familyRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0403"), familyRoom.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0503"), familyRoom.getRoomTypeId());

        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0104"), juniorSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0204"), juniorSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0304"), juniorSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0404"), juniorSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0504"), juniorSuite.getRoomTypeId());

        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0105"), grandSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0205"), grandSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0305"), grandSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0405"), grandSuite.getRoomTypeId());
        roomSessionBean.createRoomWithExistingRoomType(new RoomEntity("0505"), grandSuite.getRoomTypeId());

    }

}
