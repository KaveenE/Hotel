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
import entity.ReservationEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import util.exception.DoesNotExistException;
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

    private final BossHelper scanner;
    private final DateTimeFormatter dtf;
    private LocalDate checkOut;

    public FrontOfficeModule() {
        this.scanner = BossHelper.getSingleton();
        this.dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public FrontOfficeModule(RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ReservationSessionBeanRemote reservationSessionBean) {
        this();
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
    }

    public void menuFrontOffice() {
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
                    if (!roomTypeResults.isEmpty()) {
                        walkInReserveRoom(roomTypeResults);
                    }
                } else if (response == 3) {
                    checkInGuest();
                    System.out.println("not implemented yet!");
                } else if (response == 4) {
                    checkOutGuest();
                    System.out.println("not implemented yet!");
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
            System.out.println("*** HoRS :: Hotel Administration Client :: Search Hotel Room ***\n");
            System.out.print("Enter Check Out Date (dd-mm-yyyy)> ");
            checkOut = LocalDate.parse(scanner.nextLine(), dtf);

            availableRoomsForRoomType = roomTypeSessionBean.walkInSearchRoomTypeReservableQuantity(checkOut);

            if (availableRoomsForRoomType.values().isEmpty()) {
                System.out.println("All rooms are fully booked! Please try again later");
                bufferScreenForUser();
                return availableRoomsForRoomType;
            }

            for (Map.Entry<String, Integer> entry : availableRoomsForRoomType.entrySet()) {
                System.out.printf("%12s rooms available: %12s\n", entry.getKey(), entry.getValue());
            }

            bufferScreenForUser();

        } catch (DateTimeParseException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

        return availableRoomsForRoomType;
    }

    //TODO
    public void walkInReserveRoom(Map<String, Integer> availableRoomsForRoomType) {
        String bookingRoomType;
        Long bookingRoomTypeQuantity;
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
           reservation = reservationSessionBean.walkInReserveRoomsByRoomType(reservation, bookingRoomType, bookingRoomTypeQuantity);
            System.out.println(reservation);
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

    }

    //TODO 
    public void checkInGuest() {

    }

    //TODO
    public void checkOutGuest() {

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
