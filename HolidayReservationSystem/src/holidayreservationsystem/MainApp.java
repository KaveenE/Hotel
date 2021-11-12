/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import javax.validation.ConstraintViolationException;
import javax.xml.datatype.DatatypeConfigurationException;
import util.helper.BossHelper;
import ws.client.BeanValidationException_Exception;
import ws.client.DoesNotExistException_Exception;
import ws.client.InvalidLoginException_Exception;
import ws.client.Pair;
import ws.client.PartnerEntity;
import ws.client.PartnerWebService;
import ws.client.PartnerWebService_Service;
import ws.client.ReservationEntity;

/**
 *
 * @author PP42
 */
public class MainApp {

    private final SimpleDateFormat sdf;
    private PartnerEntity partnerEntity;
    private Date checkIn;
    private Date checkOut;
    private final BossHelper scanner;

    public MainApp() {
        this.scanner = BossHelper.getSingleton();
        this.sdf = new SimpleDateFormat("dd-MM-yyyy");
    }

    public void runApp() {

        Integer response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Partner Search Room");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        mainMenu();
                    } catch (InvalidLoginException_Exception ex) {
                        bufferScreenForUser(ex.getMessage());
                    }
                } else if (response == 2) {
                    partnerSearchRoom();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    public void doLogin() throws InvalidLoginException_Exception {
        String username;
        String password;

        System.out.println("*** Holiday Reservation System :: Employee Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            this.partnerEntity = partnerLogin(username, password);
        } else {
            System.out.println("Missing login credential!");
        }
    }

    public List<Pair> partnerSearchRoom() {

        List<Pair> availableRoomsForRoomType = null;

        try {
            System.out.println("*** HoRS :: Hotel Reservation Client :: Search Hotel Room ***\n");
            System.out.print("Enter Check In Date (dd-mm-yyyy)>");
            
            checkIn = sdf.parse(scanner.nextLine());
            Calendar cal = Calendar.getInstance(); 
            cal.setTime(checkIn);
            cal.add(Calendar.HOUR_OF_DAY, LocalDateTime.now().getHour());
            checkIn = cal.getTime();
            
            System.out.print("Enter Check Out Date (dd-mm-yyyy)>");
            checkOut = sdf.parse(scanner.nextLine());

            availableRoomsForRoomType = searchRoomTypeReservableQuantity(checkIn, checkOut);
            if (availableRoomsForRoomType.isEmpty()) {
                System.out.println("All rooms are fully booked! Please try again later");
                bufferScreenForUser();
                return availableRoomsForRoomType;
            }
            for (Pair entry : availableRoomsForRoomType) {
                System.out.printf("%35s rooms available: %12s\n", entry.getFirst(), entry.getSecond());
            }
            bufferScreenForUser();

        } catch (DateTimeException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        } catch (ParseException | DatatypeConfigurationException | DoesNotExistException_Exception ex) {
            bufferScreenForUser(ex.getMessage());
        }

        return availableRoomsForRoomType;
    }

    public void mainMenu() {
        Integer response = 0;

        while (true) {
            System.out.println("*** Holiday Reservation system ***\n");
            System.out.println("You are logged in as " + partnerEntity.getUsername() + "\n");

            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Logout\n");

            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    List<Pair> roomTypeResults = partnerSearchRoom();
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

    public void reserveHotelRoom(List<Pair> availableRoomsForRoomType) {

        String bookingRoomType;
        Long bookingRoomTypeQuantity;
        System.out.println("*** Holiday Reservation system :: Reserve Hotel Room ***\n");

        System.out.print("Enter Room Type to book> ");
        bookingRoomType = scanner.nextLine();
        System.out.print("Enter number of rooms> ");
        bookingRoomTypeQuantity = scanner.nextLong();

        if (!containsKey(availableRoomsForRoomType, bookingRoomType)) {
            bufferScreenForUser("Room Type does not exist");
            return;
        } else if (bookingRoomTypeQuantity > getOrDefault(availableRoomsForRoomType, bookingRoomType, -1)) {
            bufferScreenForUser("Insufficient rooms for requested room type!");
            return;
        }

        try {
            reserveRoomsByRoomType(checkIn, checkOut, bookingRoomType, bookingRoomTypeQuantity, partnerEntity.getUsername());
        } catch (DatatypeConfigurationException | DoesNotExistException_Exception | BeanValidationException_Exception ex) {
            bufferScreenForUser(ex.getMessage());
        }
    }

    public void viewMyReservationDetails() {
        Long reservationId;
        System.out.println("*** Holiday Reservation system :: View Reservation Detail ***\n");

        System.out.print("Enter Reservation Id to View> ");
        reservationId = scanner.nextLong();
        try {
            ReservationEntity reservationEntity = retrieveReservationsByPartner(partnerEntity.getUsername(), reservationId);

            System.out.printf("%20s%20s%20s%20s\n", "Reservation Id", "Check-In Date", "Check-Out Date", "Price of Stay");
            System.out.printf("%20s%20s%20s%20s\n",
                    reservationEntity.getReservationId(), BossHelper.XMLDateToLocalDate(reservationEntity.getCheckInDate()),
                    BossHelper.XMLDateToLocalDate(reservationEntity.getCheckOutDate()), reservationEntity.getPriceOfStay());

        } catch (DoesNotExistException_Exception | DatatypeConfigurationException ex) {
            bufferScreenForUser(ex.getMessage());
        }
    }

    public void viewAllMyReservations() {
        try {
            List<ReservationEntity> reservationEntities = retrieveAllReservationsByPartner(partnerEntity.getUsername());
            System.out.println("*** HoRS :: Hotel Reservation Client :: View All Reservations ***\n");
            System.out.printf("%20s%20s%20s%20s\n", "Reservation Id", "Check-In Date", "Check-Out Date", "Price of Stay");
            for (ReservationEntity reservationEntity : reservationEntities) {
                System.out.printf("%20s%20s%20s%20s\n",
                        reservationEntity.getReservationId(), BossHelper.XMLDateToLocalDate(reservationEntity.getCheckInDate()),
                        BossHelper.XMLDateToLocalDate(reservationEntity.getCheckOutDate()), reservationEntity.getPriceOfStay());
            }
        } catch (DoesNotExistException_Exception | DatatypeConfigurationException ex) {
            bufferScreenForUser(ex.getMessage());
        }
    }

    private boolean containsKey(List<Pair> availableRoomsForRoomType, String bookingRoomType) {
        return availableRoomsForRoomType
                .stream()
                .anyMatch(x -> x.getFirst().equals(bookingRoomType));
    }

    private Integer getOrDefault(List<Pair> availableRoomsForRoomType, String bookingRoomType, int defaultValue) {
        for (Pair pair : availableRoomsForRoomType) {
            if (pair.getFirst().equals(bookingRoomType)) {
                return (Integer) pair.getSecond();
            }
        }
        return defaultValue;
    }

    private void bufferScreenForUser(String message) {
        System.out.println(message);
        this.bufferScreenForUser();
    }

    private void bufferScreenForUser() {
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(username, password);
    }

    public void reserveRoomsByRoomType(Date checkIn, Date checkOut, String roomTypeName, Long roomQuantity, String username)
            throws DoesNotExistException_Exception, DatatypeConfigurationException, BeanValidationException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        port.reserveRoomsByRoomType(BossHelper.DateToXMLDate(checkIn), BossHelper.DateToXMLDate(checkOut), roomTypeName, roomQuantity, username);
    }

    public List<Pair> searchRoomTypeReservableQuantity(Date checkIn, Date checkOut) throws DoesNotExistException_Exception, DatatypeConfigurationException {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.searchRoomTypeReservableQuantityForPartner(BossHelper.DateToXMLDate(checkIn), BossHelper.DateToXMLDate(checkOut));
    }

    public ReservationEntity retrieveReservationsByPartner(String username, Long reservationId) throws DoesNotExistException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.retrieveReservationsByPartner(username, reservationId);
    }

    public List<ReservationEntity> retrieveAllReservationsByPartner(String username) throws DoesNotExistException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.retrieveAllReservationsByPartner(username);
    }

}
