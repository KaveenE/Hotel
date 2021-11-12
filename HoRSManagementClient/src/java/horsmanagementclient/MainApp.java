/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.EmployeeEntity;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
public class MainApp {

    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;

    private final BossHelper scanner;

    private EmployeeEntity employeeEntity;
    private HotelOperationModule hotelOperationModule;
    private SystemAdministrationModule systemAdministrationModule;
    private FrontOfficeModule frontOfficeModule;

    public MainApp() {
        this.scanner = BossHelper.getSingleton();
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBean, PartnerSessionBeanRemote partnerSessionBean, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ReservationSessionBeanRemote reservationSessionBean) {
        this();
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
    }

    public void runApp() {

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to HoRS :: Hotel Management System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        hotelOperationModule = new HotelOperationModule(roomSessionBean, roomRateSessionBean, roomTypeSessionBean, reservationSessionBean, employeeEntity);
                        systemAdministrationModule = new SystemAdministrationModule(employeeSessionBean, partnerSessionBean, employeeEntity);
                        frontOfficeModule = new FrontOfficeModule(roomSessionBean, roomTypeSessionBean, roomRateSessionBean, reservationSessionBean, employeeEntity);

                        menuMain();
                    } catch (InvalidLoginException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginException {
        Scanner scanner = new Scanner(System.in);
        String username;
        String password;

        System.out.println("*** HoRS System :: Employee Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            employeeEntity = employeeSessionBean.employeeLogin(username, password);
        } else {
            throw new InvalidLoginException("Missing login credential!");
        }
    }

    private void menuMain() {

        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Management (HoRS) System ***\n");
            System.out.println("You are logged in as " + employeeEntity.getUsername() + " with " + employeeEntity.getEmployeeRoleEnum().toString() + " rights\n");
            System.out.println("1: System Administration");
            System.out.println("2: Hotel Operation");
            System.out.println("3: Front Office");
            System.out.println("4: Logout\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                try {
                    if (response == 1) {
                        systemAdministrationModule.menuSystemOperation();
                    } else if (response == 2) {
                        hotelOperationModule.menuHotelOperation();
                    } else if (response == 3) {
                        frontOfficeModule.menuFrontOffice();
                    } else if (response == 4) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                } catch (InvalidAccessRightException ex) {
                    bufferScreenForUser(ex.getMessage());
                }
            }

            if (response == 4) {
                break;
            }
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
