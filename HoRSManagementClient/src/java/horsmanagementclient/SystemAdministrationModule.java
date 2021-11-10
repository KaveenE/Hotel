/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.EmployeeEntity;
import entity.PartnerEntity;
import java.util.List;
import util.enumeration.EmployeeRoleEnum;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
public class SystemAdministrationModule {

    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private final BossHelper scanner;

    public SystemAdministrationModule() {
        this.scanner = BossHelper.getSingleton();
    }

    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote) {
        this();
        this.employeeSessionBean = employeeSessionBeanRemote;
        this.partnerSessionBean = partnerSessionBeanRemote;
    }

    public void menuSystemOperation() {
        Integer response = 0;
        while (true) {
            System.out.println("*** HoRS :: System Operation ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createEmployee();
                } else if (response == 2) {
                    viewAllEmployees();
                } else if (response == 3) {
                    createPartner();
                } else if (response == 4) {
                    viewAllPartners();
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

    public void createEmployee() {
        try {
            Integer response = 0;

            EmployeeEntity newEmployee = new EmployeeEntity();

            System.out.println("*** HoRS :: Hotel Administration System :: Create New Employee ***\n");
            System.out.print("Enter Username> ");
            newEmployee.setUsername(scanner.nextLine());
            System.out.print("Enter Password> ");
            newEmployee.setPassword(scanner.nextLine());

            while (true) {
                System.out.print("Select Access Right (1: System Operator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
                response = scanner.nextInt();

                if (response >= 1 && response <= 4) {
                    newEmployee.setEmployeeRoleEnum(EmployeeRoleEnum.values()[response - 1]);
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            newEmployee = employeeSessionBean.createNewEmployee(newEmployee);
            System.out.println("New employee created successfully!: " + newEmployee.getUsername() + "\n");

        } catch (AlreadyExistsException | UnknownPersistenceException | BeanValidationException ex) {
            System.out.println(ex.getMessage() + "!\n");
        }
    }

    public void viewAllEmployees() {

        System.out.println("*** HoRS :: Hotel Administration System :: View All Employees ***\n");

        List<EmployeeEntity> employeeEntities = employeeSessionBean.retrieveAllEmployees();
        System.out.printf("%12s%20s%30s\n", "Emplyoee Id", "Username", "Employee Role");

        for (EmployeeEntity employeeEntity : employeeEntities) {
            System.out.printf("%12s%20s%30s\n", employeeEntity.getEmployeeId().toString(), employeeEntity.getUsername(), employeeEntity.getEmployeeRoleEnum().toString());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void createPartner() {
        try {
            Integer response = 0;

            PartnerEntity newPartner = new PartnerEntity();

            System.out.println("*** HoRS :: Hotel Administration System :: Create New Partner ***\n");
            System.out.print("Enter Username> ");
            newPartner.setUsername(scanner.nextLine());
            System.out.print("Enter Password> ");
            newPartner.setPassword(scanner.nextLine());

            newPartner = partnerSessionBean.createNewPartner(newPartner);
            System.out.println("New partner created successfully!: " + newPartner.getUsername() + "\n");

        } catch (AlreadyExistsException | UnknownPersistenceException | BeanValidationException ex) {
            System.out.println(ex.getMessage() + "!\n");
        }
    }

    public void viewAllPartners() {
        System.out.println("*** HoRS :: Hotel Administration System :: View All Partners ***\n");

        List<PartnerEntity> partnerEntities = partnerSessionBean.retrieveAllPartners();
        System.out.printf("%12s%20s\n", "Partner Id", "Username");

        for (PartnerEntity partnerEntity : partnerEntities) {
            System.out.printf("%12s%20s\n", partnerEntity.getPartnerId().toString(), partnerEntity.getUsername());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

}
