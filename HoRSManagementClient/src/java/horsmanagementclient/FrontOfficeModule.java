/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import entity.ReservationEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import util.enumeration.EmployeeRoleEnum;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.InvalidAccessRightException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
public class FrontOfficeModule {

    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private EmployeeEntity employeeEntity;

    private final BossHelper scanner;
    private final DateTimeFormatter dtf;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public FrontOfficeModule() {
        this.scanner = BossHelper.getSingleton();
        this.dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public FrontOfficeModule(RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ReservationSessionBeanRemote reservationSessionBean, EmployeeEntity employeeEntity) {
        this();
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.employeeEntity = employeeEntity;
    }

    public void menuFrontOffice() throws InvalidAccessRightException {
        if (employeeEntity.getEmployeeRoleEnum() != EmployeeRoleEnum.GUEST_RELATION_OFFICER || employeeEntity.getEmployeeRoleEnum() != EmployeeRoleEnum.SYSTEM_ADMINISTRATOR) {
            throw new InvalidAccessRightException("You don't have GUEST RELATION OFFICER rights to access the system administration module.");
        }
        
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS :: Front Office Module ***\n");
            System.out.println("1: Walk-In Search Room");
            System.out.println("2: Walk-In Reserve Room");
            System.out.println("3: Check-In Guest");
            System.out.println("4: Check-Out Guest");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    walkInSearchRoom();
                } else if (response == 2) {
                    Map<String, Integer> roomTypeResults = walkInSearchRoom();
                    if (roomTypeResults != null && !roomTypeResults.isEmpty()) {
                        walkInReserveRoom(roomTypeResults);
                    }
                } else if (response == 3) {
                    checkInGuest();
                } else if (response == 4) {
                    checkOutGuest();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }

    public Map<String, Integer> walkInSearchRoom() {

        Map<String, Integer> availableRoomsForRoomType = null;

        try {
            System.out.println("*** HoRS :: Hotel Management Client :: Search Hotel Room ***\n");
            System.out.print("Enter Check In Date (dd-mm-yyyy)> ");
            checkIn = LocalDate.parse(scanner.nextLine(), dtf);
            System.out.print("Enter Check Out Date (dd-mm-yyyy)> ");
            checkOut = LocalDate.parse(scanner.nextLine(), dtf);

            availableRoomsForRoomType = roomTypeSessionBean.searchRoomTypeReservableQuantity(checkIn, checkOut);

            if (availableRoomsForRoomType.values().isEmpty()) {
                System.out.println("All rooms are fully booked! Please try again later");
                bufferScreenForUser();
                return availableRoomsForRoomType;
            }

            for (Map.Entry<String, Integer> entry : availableRoomsForRoomType.entrySet()) {
                System.out.printf("%35s rooms available: %12s\n", entry.getKey(), entry.getValue());
            }

            bufferScreenForUser();

        } catch (DateTimeParseException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

        return availableRoomsForRoomType;
    }

    public void walkInReserveRoom(Map<String, Integer> availableRoomsForRoomType) {
        String bookingRoomType;
        Long bookingRoomTypeQuantity;
        System.out.println("*** HoRS :: Hotel Management Client :: Reserve Hotel Room ***\n");

        System.out.print("Enter Room Type to book> ");
        bookingRoomType = scanner.nextLine();
        System.out.print("Enter number of rooms> ");
        bookingRoomTypeQuantity = scanner.nextLong();

        if (!availableRoomsForRoomType.containsKey(bookingRoomType)) {
            bufferScreenForUser("Room Type does not exist");
            return;
        } else if (bookingRoomTypeQuantity > availableRoomsForRoomType.getOrDefault(bookingRoomType, -1)) {
            bufferScreenForUser("Insufficient rooms for requested room type!");
            return;
        }

        ReservationEntity reservation = new ReservationEntity(new Date(), BossHelper.localDatetoDate(checkOut));
        try {
            reservationSessionBean.walkInReserveRoomsByRoomType(reservation, bookingRoomType, bookingRoomTypeQuantity);
        } catch (DoesNotExistException | BeanValidationException ex) {
            bufferScreenForUser(ex.getMessage());
        }

    }

    public void checkInGuest() {
        try {
            Long reservationId;
            System.out.println("*** HoRS :: Hotel Management Client :: Check-in Guest ***\n");

            System.out.print("Enter Reservation Id to Check-in> ");
            reservationId = scanner.nextLong();
            ReservationEntity reservationEntity = reservationSessionBean.retrieveReservationById(reservationId);
            if (!reservationEntity.getIsAllocated()) {
                System.out.println("An exception occured during room allocation: " + reservationEntity.getExceptionReport(false));
            } else {
                System.out.printf("%15s%35s%20s\n", "Reservation Id", "Room Type", "Allocated Room");
                System.out.printf("%15s%35s%20s\n", reservationEntity.getReservationId().toString(), reservationEntity.getRoomTypeEntity().getName(), reservationEntity.getRoomEntity().getFloorUnitNo());
            }

            System.out.printf("Reservation %s checked-in successfully!\n", reservationEntity.getReservationId());

        } catch (DoesNotExistException ex) {
            bufferScreenForUser("An error has occurred while retrieving Reservation details" + ex.getMessage() + "\n");
        }
    }

    public void checkOutGuest() {
        try {
            Long reservationId;
            System.out.println("*** HoRS :: Hotel Management Client :: Check-out Guest ***\n");

            System.out.print("Enter Reservation Id to Check-out> ");
            reservationId = scanner.nextLong();

            reservationSessionBean.checkOut(reservationId);

            System.out.printf("Reservation %s checked-out successfully!\n", reservationId);
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

    }

    private void bufferScreenForUser(String message) {
        System.out.println(message);
        this.bufferScreenForUser();
    }

    private void bufferScreenForUser() {
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
