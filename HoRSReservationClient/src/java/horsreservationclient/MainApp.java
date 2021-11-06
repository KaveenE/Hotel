/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.GuestEntity;
import entity.GuestReservationEntity;
import entity.ReservationEntity;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
public class MainApp {

    private GuestSessionBeanRemote guestSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;

    private final BossHelper scanner;

    private GuestEntity guestEntity;
    private final DateTimeFormatter dtf;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public MainApp() {
        this.scanner = BossHelper.getSingleton();
        this.dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    }

    public MainApp(GuestSessionBeanRemote guestSessionBean, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ReservationSessionBeanRemote reservationSessionBean) {
        this();
        this.guestSessionBean = guestSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
    }

    public void runApp() {
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to HoRS :: Hotel Reservation Client ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        mainMenu();
                    } catch (InvalidLoginException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    createGuest();
                } else if (response == 3) {
                    guestSearchRoom();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginException {
        String username;
        String password;

        System.out.println("*** HoRS System :: Guest Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            this.guestEntity = guestSessionBean.guestLogin(username, password);
        } else {
            throw new InvalidLoginException("Missing login credential!");
        }
    }

    private void createGuest() {

        try {
            Integer response = 0;

            GuestEntity newGuest = new GuestEntity();

            System.out.println("*** HoRS :: Hotel Reservation Client :: Create New Guest ***\n");
            System.out.print("Enter Email> ");
            newGuest.setEmailAddress(scanner.nextLine());
            System.out.print("Enter Password> ");
            newGuest.setPassword(scanner.nextLine());

            newGuest = guestSessionBean.createNewGuest(newGuest);
            System.out.println("New guest created successfully!: " + newGuest.getEmailAddress() + "\n");

        } catch (AlreadyExistsException | UnknownPersistenceException ex) {
            System.out.println(ex.getMessage() + "!\n");
        }
    }

    public Map<String, Integer> guestSearchRoom() {

        Map<String, Integer> availableRoomsForRoomType = null;

        try {
            System.out.println("*** HoRS :: Hotel Reservation Client :: Search Hotel Room ***\n");
            System.out.print("Enter Check In Date (dd-mm-yyyy)>");
            checkIn = LocalDate.parse(scanner.nextLine(), dtf);
            System.out.print("Enter Check Out Date (dd-mm-yyyy)>");
            checkOut = LocalDate.parse(scanner.nextLine(), dtf);

            ReservationEntity customerBooking = new ReservationEntity(BossHelper.localDatetoDate(checkIn), BossHelper.localDatetoDate(checkOut));

            availableRoomsForRoomType = roomTypeSessionBean.searchRoomTypeReservableQuantity(checkIn, checkOut);

            if (availableRoomsForRoomType.values().isEmpty()) {
                System.out.println("All rooms are fully booked! Please try again later");
                bufferScreenForUser();
                return availableRoomsForRoomType;
            }

            for (Map.Entry<String, Integer> entry : availableRoomsForRoomType.entrySet()) {
                System.out.printf("%12s rooms available: %12s\n", entry.getKey(), entry.getValue());
            }

            bufferScreenForUser();

        } catch (DateTimeException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

        return availableRoomsForRoomType;
    }

    public void mainMenu() {
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation Client ***\n");
            System.out.println("You are logged in as " + guestEntity.getEmailAddress() + "\n");

            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");

            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    Map<String, Integer> roomTypeResults = guestSearchRoom();
                    if (!roomTypeResults.isEmpty()) {
                        reserveHotelRoom(roomTypeResults);
                    }
                } else if (response == 2) {
                    viewMyReservationDetails();
                } else if (response == 3) {
                    viewAllMyReservations();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void reserveHotelRoom(Map<String, Integer> availableRoomsForRoomType) {
        String bookingRoomType;
        Long bookingRoomTypeQuantity;
        System.out.println("*** HoRS :: Hotel Reservation Client :: Reserve Hotel Room ***\n");

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

        ReservationEntity reservation = new GuestReservationEntity(BossHelper.localDatetoDate(checkIn), BossHelper.localDatetoDate(checkOut));
        try {
            reservationSessionBean.reserveRoomsByRoomType(reservation, bookingRoomType, bookingRoomTypeQuantity, guestEntity.getEmailAddress());
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }

    }

    public void viewMyReservationDetails() {
        Long reservationId;
        System.out.println("*** HoRS :: Hotel Reservation Client :: View Reservation Detail ***\n");

        System.out.print("Enter Reservation Id to View> ");
        reservationId = scanner.nextLong();
        try {
            ReservationEntity reservationEntity = guestSessionBean.retrieveReservationsByGuest(guestEntity.getEmailAddress(), reservationId);
            System.out.printf("%20s%20s%20s%20s\n", "Reservation Id", "Check-In Date", "Check-Out Date", "Price of Stay");
            System.out.printf("%20s%20s%20s%20s\n",
                    reservationEntity.getReservationId(), BossHelper.dateToLocalDate(reservationEntity.getCheckInDate()),
                    BossHelper.dateToLocalDate(reservationEntity.getCheckOutDate()), reservationEntity.getPriceOfStay());

        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage());
        }
    }

    public void viewAllMyReservations() {
        try {
            List<ReservationEntity> reservationEntities = guestSessionBean.retrieveAllReservationsByGuest(guestEntity.getEmailAddress());
            System.out.println("*** HoRS :: Hotel Reservation Client :: View All Reservations ***\n");
            System.out.printf("%20s%20s%20s%20s\n", "Reservation Id", "Check-In Date", "Check-Out Date", "Price of Stay");
            for (ReservationEntity reservationEntity : reservationEntities) {
                System.out.printf("%20s%20s%20s%20s\n",
                        reservationEntity.getReservationId(), BossHelper.dateToLocalDate(reservationEntity.getCheckInDate()),
                        BossHelper.dateToLocalDate(reservationEntity.getCheckOutDate()), reservationEntity.getPriceOfStay());
            }
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
