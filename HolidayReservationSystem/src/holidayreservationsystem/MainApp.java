/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.InvalidLoginException_Exception;
import ws.client.PartnerEntity;
import ws.client.PartnerWebService;
import ws.client.PartnerWebService_Service;

/**
 *
 * @author SCXY
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

    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginException_Exception {
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(username, password);
    }

}
