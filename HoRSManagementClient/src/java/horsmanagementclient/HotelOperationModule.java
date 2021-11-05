/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.NormalRateEntity;
import entity.PeakRateEntity;
import entity.PromoRateEntity;
import entity.PublishedRateEntity;
import entity.RoomEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.enumeration.RoomStatusEnum;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
public class HotelOperationModule {

    private RoomSessionBeanRemote roomSessionBean;
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    private RoomRateSessionBeanRemote roomRateSessionBean;
    private final BossHelper scanner;
    private final SimpleDateFormat sdf;

    public HotelOperationModule() {
        scanner = BossHelper.getSingleton();
        sdf = new SimpleDateFormat("dd-MM-yyyy");
    }

    HotelOperationModule(RoomSessionBeanRemote roomSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean) {
        this();
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
    }

    public void menuHotelOperation() {

        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS :: Hotel Operation Module***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("-----------------------");
            System.out.println("11: Create New Room Rate");
            System.out.println("12: View Room Rate Details");
            System.out.println("13: Update Room Rate");
            System.out.println("14: Delete Room Rate");
            System.out.println("15: View All Room Rates");
            System.out.println("16: Allocate room");
            System.out.println("-----------------------");
            System.out.println("17: Back\n");
            response = 0;

            while (response < 1 || response > 16) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    createRoomType();
                } else if (response == 2) {
                    viewRoomTypeDetails();
                } else if (response == 3) {
                    System.out.print("Enter Room Type Name> ");
                    String name = scanner.nextLine();
                    try {
                        updateRoomType(roomTypeSessionBean.retrieveRoomTypeByName(name));
                    } catch (DoesNotExistException ex) {
                        System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
                    }
                } else if (response == 4) {
                    System.out.print("Enter Room Type Name> ");
                    String name = scanner.nextLine();
                    try {
                        deleteRoomType(roomTypeSessionBean.retrieveRoomTypeByName(name));
                    } catch (DoesNotExistException ex) {
                        System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
                    }
                } else if (response == 5) {
                    viewAllRoomTypes();
                } else if (response == 6) {
                    createRoom();
                } else if (response == 7) {
                    updateRoom();
                } else if (response == 8) {
                    deleteRoom();
                } else if (response == 9) {
                    viewAllRooms();
                } else if (response == 10) {
                    System.out.println("not supported yet!");
//                    viewRoomAllocationExceptionReport();
                } else if (response == 11) {
                    createRoomRate();
                } else if (response == 12) {
                    viewRoomRateDetails();
                } else if (response == 13) {
                    System.out.print("Enter Room Rate Id> ");
                    Long rateId = scanner.nextLong();
                    try {
                        updateRoomRate(roomRateSessionBean.retrieveRoomRateById(rateId));
                    } catch (DoesNotExistException ex) {
                        System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
                    }
                } else if (response == 14) {
                    System.out.print("Enter Room Rate Id> ");
                    Long rateId = scanner.nextLong();

                    try {
                        deleteRoomRate(roomRateSessionBean.retrieveRoomRateById(rateId));
                    } catch (DoesNotExistException ex) {
                        System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");

                    }

                } else if (response == 15) {
                    viewAllRoomRates();
                } else if (response == 16) {
                    System.out.println("not supported yet!");
//                    allocateRoom();
                } else if (response == 17) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 17) {
                break;
            }
        }
    }

    public void createRoomType() {

        RoomTypeEntity newRoomType = new RoomTypeEntity();

        System.out.println("*** HoRS :: Hotel Operation Module :: Create New Room Type ***\n");
        System.out.print("Enter Name> ");
        newRoomType.setName(scanner.nextLine());
        System.out.print("Enter Ranking> ");
        newRoomType.setRanking(scanner.nextInt());
        System.out.print("Enter Description> ");
        newRoomType.setDescription(scanner.nextLine());
        System.out.print("Enter Room Size (square meters)> ");
        newRoomType.setMySize(scanner.nextLong());
        System.out.print("Enter Bed> ");
        newRoomType.setBed(scanner.nextLine());
        System.out.print("Enter Capacity> ");
        newRoomType.setCapacity(scanner.nextInt());

        System.out.print("Enter Number of amenities> ");
        int numOfAmenities = scanner.nextInt();
        List<String> amenities = new ArrayList<>();
        for (int i = 1; i <= numOfAmenities; i++) {
            System.out.print("Enter Amenity #" + i + "> ");
            amenities.add(scanner.nextLine());
        }
        newRoomType.setAmenities(amenities);

        try {
            newRoomType = roomTypeSessionBean.createRoomType(newRoomType);
            System.out.println("New room type created successfully!: " + newRoomType.getRoomTypeId() + "\n");

        } catch (DoesNotExistException | UnknownPersistenceException | AlreadyExistsException ex) {
            bufferScreenForUser(ex.getMessage());
        }

    }

    public void viewRoomTypeDetails() {
        Integer response = 0;

        System.out.println("*** HoRS :: Hotel Operation Module :: View Room Type Details ***\n");

        System.out.print("Enter Room Type Name> ");
        String name = scanner.nextLine();

        try {
            RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeByName(name);

            System.out.println("\n" + "Room Type name: " + roomType.getName());
            System.out.println("Room Type description: " + roomType.getDescription());
            System.out.println("Room Type size: " + roomType.getMySize());
            System.out.println("Room Type bed: " + roomType.getBed());
            System.out.println("Room Type capacity: " + roomType.getCapacity());
            System.out.println("Room Type amenities: " + roomType.getAmenities());
            System.out.printf("Room Type status: %s\n", roomType.getIsDisabled() ? "Disabled" : "Not Disabled");
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();;

            if (response == 1) {
                updateRoomType(roomType);
            } else if (response == 2) {
                deleteRoomType(roomType);
            }
        } catch (DoesNotExistException ex) {
            bufferScreenForUser("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
        }
    }

    public void updateRoomType(RoomTypeEntity roomType) {

        String input;

        System.out.println("*** HoRS :: Hotel Operation Module :: Update Room Type ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() > 0) {
            roomType.setName(input);
        }
        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() > 0) {
            roomType.setDescription(input);
        }
        System.out.print("Enter Size - in square meters (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() > 0) {
            while (true) {
                try {
                    roomType.setMySize(Long.parseLong(input));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number");
                }
            }
        }
        System.out.print("Enter Bed (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() > 0) {
            roomType.setBed(input);
        }
        System.out.print("Enter Capacity (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() > 0) {
            while (true) {
                try {
                    roomType.setCapacity(Integer.parseInt(input));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number");
                }
            }
        }

        System.out.print("Enter New Number of amenities (blank if no change)>");
        input = scanner.nextLine();

        if (!input.isEmpty()) {
            int numOfAmenities = Integer.valueOf(input);
            List<String> amenities = new ArrayList<>();
            for (int i = 1; i <= numOfAmenities; i++) {
                System.out.print("Enter Amenity #" + i + "> ");
                amenities.add(scanner.nextLine());
            }
            roomType.setAmenities(amenities);
        }

        System.out.print("Enter Room Rank (blank if no change)> ");
        input = scanner.nextLine();
        if (input.length() != 0) {
            roomType.setRanking(Integer.valueOf(input));
        }

        try {
            roomTypeSessionBean.updateRoomType(roomType);
            System.out.println("Room Type updated successfully!\n");
        } catch (DoesNotExistException | AlreadyExistsException ex) {
            bufferScreenForUser("An error has occurred while updating room type: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomType(RoomTypeEntity roomType) {

        String input;

        System.out.println("*** HoRS :: Hotel Operation Module :: Delete Room Type ***\n");
        System.out.printf("Confirm Delete Room Type %s (Room Type ID: %d) (Enter 'Y' to Delete)> ", roomType.getName(), roomType.getRoomTypeId());
        input = scanner.nextLine().toUpperCase();

        if (input.equals("Y")) {
            try {
                roomTypeSessionBean.deleteRoomTypeByName(roomType.getName());
                System.out.println("Room Type deleted successfully!\n");
            } catch (DoesNotExistException ex) {
                bufferScreenForUser("An error has occurred while deleting Room Type: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    public void viewAllRoomTypes() {

        System.out.println("*** HoRS :: Hotel Operation Module :: View All Room Type ***\n");

        List<RoomTypeEntity> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", "Room Type Id", "Name", "Description", "Size", "Bed", "Capacity", "Amenities", "Disabled");

        for (RoomTypeEntity roomType : roomTypes) {
            System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", roomType.getRoomTypeId().toString(), roomType.getName(), roomType.getDescription(), roomType.getMySize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities(), roomType.getIsDisabled());
        }

        bufferScreenForUser();
    }

    public void createRoom() {
        try {

            RoomEntity newRoom = new RoomEntity();

            System.out.println("*** HoRS :: Hotel Operation Module :: Create New Room ***\n");
            System.out.print("Enter Room Number (room floor + room number)> ");
            newRoom.setFloorUnitNo(scanner.nextLine());
            System.out.print("Enter Room Type Name> ");
            String roomTypeName = scanner.nextLine();
            RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
            System.out.println("Open room for room type: " + roomType.getName() + "\n");
            newRoom = roomSessionBean.createRoomWithExistingRoomType(newRoom, roomType.getRoomTypeId());
            System.out.println("New room created successfully!: " + newRoom.getFloorUnitNo() + "\n");
        } catch (DoesNotExistException | UnknownPersistenceException | AlreadyExistsException ex) {
            bufferScreenForUser(ex.getMessage() + "!\n");
        }
    }

    //I'm not sure abt this method as you would have noticed from the telegram msg
    public void updateRoom() {
        try {

            String input;

            System.out.println("*** HoRS :: Hotel Operation Module :: Update Room ***\n");
            System.out.print("Enter Room Number> ");
            String roomNumber = scanner.nextLine();
            RoomEntity room = roomSessionBean.retrieveRoomByFloorUnitNo(roomNumber);

            System.out.print("Enter Room Floor Unit Number (blank if no change)> ");
            input = scanner.nextLine();
            if (input.length() != 0) {
                room.setFloorUnitNo(input);
            }

            System.out.print("Choose Room Status (1. available, 2.unavailable)> ");
            Integer roomStatusInt = scanner.nextInt();;
            if (roomStatusInt >= 1 && roomStatusInt <= 2) {
                room.setRoomStatusEnum(RoomStatusEnum.values()[roomStatusInt - 1]);
                roomSessionBean.updateRoom(room);
            } else {
                System.out.println("Invalid option!\n");
            }

        } catch (DoesNotExistException | AlreadyExistsException ex) {
            bufferScreenForUser("An error has occurred while updating room : " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoom() {
        try {
            String input;

            System.out.println("*** HoRS :: Hotel Operation Module :: Delete Room ***\n");
            System.out.print("Enter Room Number> ");
            String roomNumber = scanner.nextLine();
            RoomEntity room = roomSessionBean.retrieveRoomByFloorUnitNo(roomNumber);
            System.out.printf("Confirm Delete Room Number %s (Enter 'Y' to Delete)> ", room.getFloorUnitNo());
            input = scanner.nextLine().toUpperCase();

            if (input.equals("Y")) {
                roomSessionBean.deleteRoomByFloorUnitNo(roomNumber);
                System.out.println("Room deleted successfully!\n");
            } else {
                System.out.println("Room NOT deleted!\n");
            }
        } catch (DoesNotExistException ex) {
            bufferScreenForUser("An error has occurred while deleting Room : " + ex.getMessage() + "\n");
        }
    }

    public void viewAllRooms() {

        System.out.println("*** HoRS :: Hotel Operation Module :: View All Rooms ***\n");

        List<RoomEntity> rooms = roomSessionBean.retrieveAllRooms();
        System.out.printf("%12s%12s%20s%20s\n", "Room Number", "Room Status", "Room Type", "Room Reservation Id");
        for (RoomEntity room : rooms) {
            System.out.printf("%12s%12s%20s\n", room.getFloorUnitNo(), room.getRoomStatusEnum(),
                    room.getRoomTypeEntity().getName());
        }
       bufferScreenForUser();
    }

    public void createRoomRate() {
        try {

            RoomRateAbsEntity newRoomRate;

            System.out.println("*** HoRS :: Hotel Operation Module :: Create New Room Rate ***\n");
            while (true) {
                System.out.print("Select Room Rate Type:\n 1. Normal Rate\n 2. Published Rate\n 3. Promotion Rate\n 4. Peak Rate\n> ");
                Integer roomRateTypeInt = scanner.nextInt();
                if (roomRateTypeInt >= 1 && roomRateTypeInt <= 4) {
                    if (roomRateTypeInt == 1) {
                        newRoomRate = new NormalRateEntity();
                        break;
                    } else if (roomRateTypeInt == 2) {
                        newRoomRate = new PublishedRateEntity();
                        break;
                    } else if (roomRateTypeInt == 3) {
                        newRoomRate = new PromoRateEntity();
                        break;
                    } else if (roomRateTypeInt == 4) {
                        newRoomRate = new PeakRateEntity();
                        break;
                    } else {
                        System.out.println("Sorry, this room rate type is currently not available. Please try again!\n");
                    }
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            System.out.print("Enter Room Rate name> ");
            newRoomRate.setName(scanner.nextLine());

            System.out.print("Enter Room Rate per night> ");
            newRoomRate.setRatePerNight(scanner.nextBigDecimal());

            System.out.print("Enter Room Type Name> ");
            String roomTypeName = scanner.nextLine();
            RoomTypeEntity roomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);

            Date date;
            if (newRoomRate instanceof PromoRateEntity) {
                System.out.print("Enter Promotion Rate start date (dd-mm-yyyy)> ");
                date = sdf.parse(scanner.nextLine());
                ((PromoRateEntity) newRoomRate).setValidFrom(date);
                System.out.print("Enter Promotion Rate end date (dd-mm-yyyy)> ");
                date = sdf.parse(scanner.nextLine());
                ((PromoRateEntity) newRoomRate).setValidTo(date);
            } else if (newRoomRate instanceof PeakRateEntity) {
                System.out.print("Enter Peak Rate start date (dd-mm-yyyy)> ");
                date = sdf.parse(scanner.nextLine());
                ((PeakRateEntity) newRoomRate).setValidFrom(date);
                System.out.print("Enter Peak Rate end date (dd-mm-yyyy)> ");
                date = sdf.parse(scanner.nextLine());
                ((PeakRateEntity) newRoomRate).setValidTo(date);
            }

            System.out.println("Open room rate for room type: " + roomType.getName() + "\n");
            newRoomRate = roomRateSessionBean.createRoomRateWithExistingRoomType(newRoomRate, roomType.getRoomTypeId());
            System.out.println("New room rate created successfully!: " + newRoomRate.getRoomRateId() + "\n");
        } catch (DoesNotExistException ex) {
            bufferScreenForUser(ex.getMessage() + "!\n");
        } catch (ParseException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        }
    }

    public void viewRoomRateDetails() {

        Integer response = 0;

        System.out.println("*** HoRS :: Hotel Operation Module :: View Room Rate Details ***\n");

        System.out.print("Enter Room Rate Id> ");
        Long rateId = scanner.nextLong();

        try {

            RoomRateAbsEntity roomRateAbsEntity = roomRateSessionBean.retrieveRoomRateById(rateId);

            System.out.println("\n" + "Room rate name: " + roomRateAbsEntity.getName());
            System.out.println("Room rate per night: " + roomRateAbsEntity.getRatePerNight().toString());
            System.out.println("Room rate type : " + roomRateAbsEntity.getRoomTypeEntity().getName());
            System.out.println("Room rate status: " + roomRateAbsEntity.getIsDisabled());

            if (roomRateAbsEntity instanceof PromoRateEntity) {
                PromoRateEntity promoRate = ((PromoRateEntity) roomRateAbsEntity);
                System.out.println("Room rate start date: " + sdf.format(promoRate.getValidFrom()));
                System.out.println("Room rate end date: " + sdf.format(promoRate.getValidTo()));
            } else if (roomRateAbsEntity instanceof PeakRateEntity) {
                PeakRateEntity peakRate = ((PeakRateEntity) roomRateAbsEntity);
                System.out.println("Room rate start date: " + sdf.format(peakRate.getValidFrom()));
                System.out.println("Room rate end date: " + sdf.format(peakRate.getValidTo()));
            }

            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                updateRoomRate(roomRateAbsEntity);
            } else if (response == 2) {
                deleteRoomRate(roomRateAbsEntity);
            }
        } catch (DoesNotExistException ex) {
            bufferScreenForUser("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
        }
    }

    public void viewAllRoomRates() {

        System.out.println("*** HoRS :: Hotel Operation Module :: View All Room Rates ***\n");

        List<RoomRateAbsEntity> roomRates = roomRateSessionBean.retrieveAllRoomRates();

        for (RoomRateAbsEntity roomRate : roomRates) {
            System.out.printf("%12s%40s%16s%10s%15s%14s%14s\n", "Room Rate Id", "Name", "Rate Per Night", "Disabled", "Room Type", "Start Date", "End Date");
            if (roomRate instanceof PromoRateEntity) {
                PromoRateEntity promoRate = (PromoRateEntity) roomRate;
                System.out.printf("%12s%40s%16s%10s%15s%14s%14s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getIsDisabled(), roomRate.getRoomTypeEntity().getName(),
                        sdf.format(promoRate.getValidFrom()), sdf.format(promoRate.getValidTo()));
            } else if (roomRate instanceof PeakRateEntity) {
                PeakRateEntity peakRate = (PeakRateEntity) roomRate;
                System.out.printf("%12s%40s%16s%10s%15s%14s%14s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getIsDisabled(), roomRate.getRoomTypeEntity().getName(),
                        sdf.format(peakRate.getValidFrom()), sdf.format(peakRate.getValidTo()));
            } else if (roomRate instanceof NormalRateEntity) {
                System.out.printf("%12s%40s%16s%10s%15s%14s%14s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getIsDisabled(), roomRate.getRoomTypeEntity().getName(),
                        "NA", "NA");
            } else if (roomRate instanceof PublishedRateEntity) {
                System.out.printf("%12s%40s%16s%10s%15s%14s%14s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getIsDisabled(), roomRate.getRoomTypeEntity().getName(),
                        "NA", "NA");
            }
        }
        bufferScreenForUser();
    }

    public void updateRoomRate(RoomRateAbsEntity roomRateAbsEntity) {
        try {

            String input;

            System.out.println("*** HoRS :: Hotel Operation Module :: Update Room Rate ***\n");
            System.out.print("Enter Name (blank if no change)> ");
            input = scanner.nextLine();
            if (input.length() > 0) {
                roomRateAbsEntity.setName(input);
            }
            System.out.print("Enter Rate per night (blank if no change)> ");
            input = scanner.nextLine();
            if (input.length() > 0) {
                roomRateAbsEntity.setRatePerNight(BigDecimal.valueOf(Double.valueOf(input)));
            }

            if (roomRateAbsEntity instanceof PromoRateEntity) {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine();
                if (input.length() > 0) {
                    ((PromoRateEntity) roomRateAbsEntity).setValidFrom(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine();
                if (input.length() > 0) {
                    ((PromoRateEntity) roomRateAbsEntity).setValidTo(sdf.parse(input));
                }
            } else if (roomRateAbsEntity instanceof PeakRateEntity) {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine();
                if (input.length() > 0) {
                    ((PeakRateEntity) roomRateAbsEntity).setValidFrom(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine();
                if (input.length() > 0) {
                    ((PeakRateEntity) roomRateAbsEntity).setValidTo(sdf.parse(input));
                }
            }

            roomRateSessionBean.updateRoomRate(roomRateAbsEntity);
            System.out.println("Room Rate updated successfully!\n");

        } catch (ParseException ex) {
            bufferScreenForUser("Invalid Date Format entered!" + "\n");
        } catch (DoesNotExistException ex) {
            bufferScreenForUser("An error has occurred while updating room rate: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomRate(RoomRateAbsEntity roomRate) {
        String input;

        System.out.println("*** HoRS :: Hotel Operation Module :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        input = scanner.nextLine().toUpperCase();

        if (input.equals("Y")) {
            try {
                roomRateSessionBean.deleteRoomRateById(roomRate.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            } catch (DoesNotExistException ex) {
                
                bufferScreenForUser("An error has occurred while deleting Room Rate: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room Rate NOT deleted!\n");
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
