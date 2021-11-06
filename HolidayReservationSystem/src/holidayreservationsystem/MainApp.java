/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.util.Map;
import java.util.Scanner;
import ws.client.InvalidLoginException_Exception;
import ws.client.PartnerEntity;
import ws.client.PartnerWebService;
import ws.client.PartnerWebService_Service;
import ws.reservation.DoesNotExistException_Exception;
import ws.reservation.ReservationEntity;
import ws.reservation.ReservationWebService;
import ws.reservation.ReservationWebService_Service;

/**
 *
 * @author PP42
 */
public class MainApp {

    private Scanner scanner;
    private PartnerEntity partnerEntity;

    public MainApp() {
        this.scanner = new Scanner(System.in);
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
                    doLogin();
                } else if (response == 2) {
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

    public void doLogin() {
        String username;
        String password;

        System.out.println("*** Holiday Reservation System :: Employee Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            try {
                this.partnerEntity = partnerLogin(username, password);
            } catch (InvalidLoginException_Exception ex) {
                System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Missing login credential!");
        }
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
//                    Map<String, Integer> roomTypeResults = guestSearchRoom();
//                    if (!roomTypeResults.isEmpty()) {
//                        reserveHotelRoom(roomTypeResults);
//                    }
                } else if (response == 2) {
//                    viewMyReservationDetails();
                } else if (response == 3) {
//                    viewAllMyReservations();
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

    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(username, password);
    }

    public void reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity, String username)
            throws DoesNotExistException_Exception {
        ReservationWebService_Service service = new ReservationWebService_Service();
        ReservationWebService port = service.getReservationWebServicePort();
        port.reserveRoomsByRoomType(reservation, roomTypeName, roomQuantity, username);
    }

}
