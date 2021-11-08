/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import entity.RoomEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.RoomStatusEnum;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.RoomRateDoesNotExistException;
import util.exception.RoomTypeAlreadyExistsException;
import util.exception.RoomTypeDoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;
import util.helper.Pair;

/**
 *
 * @author PP42
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;
    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public RoomTypeEntity retrieveRoomTypeById(Long roomTypeId) throws DoesNotExistException {
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        return BossHelper.requireNonNull(roomType, new RoomTypeDoesNotExistException());
    }

    @Override
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomTypeEntity rt");
        List<RoomTypeEntity> allRoomTypes = query.getResultList();
        allRoomTypes.stream().forEach(rt -> rt.getAmenities().size());

        return allRoomTypes;
    }

    @Override
    public void updateRoomType(RoomTypeEntity roomType) throws DoesNotExistException, AlreadyExistsException {
        BossHelper.requireNonNull(roomType, new RoomTypeDoesNotExistException());
        BossHelper.requireNonNull(roomType.getRoomTypeId(), new RoomTypeDoesNotExistException());

        RoomTypeEntity roomTypeToUpdate = retrieveRoomTypeById(roomType.getRoomTypeId());
        if (!roomTypeToUpdate.getName().equals(roomType.getName()) && uniqueFieldAlreadyExists(roomType.getName())) {
            throw new RoomTypeAlreadyExistsException("This unique name already exists!");
        }

        roomTypeToUpdate.setName(roomType.getName());
        roomTypeToUpdate.setAmenities(roomType.getAmenities());
        roomTypeToUpdate.setBed(roomType.getBed());
        roomTypeToUpdate.setCapacity(roomType.getCapacity());
        roomTypeToUpdate.setDescription(roomType.getDescription());
        roomTypeToUpdate.setMySize(roomType.getMySize());
        roomTypeToUpdate.setRanking(roomType.getRanking());

    }

    private boolean uniqueFieldAlreadyExists(String name) {
        return !em.createQuery("SELECT rt FROM RoomTypeEntity rt WHERE rt.roomConfig.name = :name")
                .setParameter("name", name)
                .getResultList()
                .isEmpty();
    }

    @Override
    public RoomTypeEntity createRoomType(RoomTypeEntity roomType) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException {
        try {
            em.persist(roomType);
            em.flush();
        } catch (PersistenceException persistenceExceptionThrown) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(persistenceExceptionThrown, new RoomTypeAlreadyExistsException());
        }

        return roomType;
    }

    @Override
    public void deleteRoomTypeByName(String name) throws DoesNotExistException {
        //Delete a particular room type record.
        //A room type record can only be deleted if it is not used. 
        //Otherwise, it should be marked as disabled and no new room should be created for disabled room type. 
        RoomTypeEntity potentialRoomTypeToDelete = retrieveRoomTypeByName(name);

        if (!potentialRoomTypeToDelete.getIsDisabled() && potentialRoomTypeToDelete.getRoomEntities().isEmpty()) {
            em.remove(potentialRoomTypeToDelete);
        } else if (!potentialRoomTypeToDelete.getIsDisabled() && !potentialRoomTypeToDelete.getRoomEntities().isEmpty()) {
            potentialRoomTypeToDelete.setIsDisabled(true);
        } else {
            throw new RoomTypeDoesNotExistException("The room type you want to delete is disabled!");
        }
    }

    @Override
    public RoomTypeEntity retrieveRoomTypeByName(String name) throws DoesNotExistException {
        try {
            RoomTypeEntity rt = (RoomTypeEntity) em.createQuery("SELECT rt FROM RoomTypeEntity rt WHERE rt.roomConfig.name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            rt.getAmenities().size();

            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeDoesNotExistException();
        }
    }

    @Override
    public RoomRateAbsEntity retrieveRoomRateFromRoomType(String roomTypeName, Class subclass) throws DoesNotExistException {
        try {
            RoomRateAbsEntity rr = (RoomRateAbsEntity) em.createQuery("SELECT rt"
                    + " FROM RoomTypeEntity rt JOIN rt.roomRateAbsEntities AS rr"
                    + " WHERE TYPE(rr) = :subclass AND rt.roomConfig.name = :roomTypeName")
                    .setParameter("subclass", subclass)
                    .setParameter("roomTypeName", roomTypeName)
                    .getSingleResult();

            return rr;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateDoesNotExistException(subclass.getName() + " for this room type does not exist");
        }
    }

    @Override
    public Map<String, Integer> walkInSearchRoomTypeReservableQuantity(LocalDate checkOut) throws DoesNotExistException {
        return searchRoomTypeReservableQuantity(LocalDate.now(), checkOut);
    }

    //Gives you the mapping of the room type and the respective allocatable quantity
    @Override
    public Map<String, Integer> searchRoomTypeReservableQuantity(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException {
        Map<String, Integer> roomTypeToAvailableRooms = new HashMap<>();

        int freeRooms;
        for (RoomTypeEntity potentialRoomTypeToReserve : retrieveAllRoomTypes()) {
            if (potentialRoomTypeToReserve.getIsDisabled()) {
                break;
            }
            freeRooms = getAllocatableQuantityByRoomType(checkIn, checkOut, potentialRoomTypeToReserve.getName());
            roomTypeToAvailableRooms.put(potentialRoomTypeToReserve.getName(), freeRooms);
        }

        return roomTypeToAvailableRooms;
    }

    @Override
    public List<Pair<String, Integer>> searchRoomTypeReservableQuantityForPartner(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException {
        Map<String, Integer> roomTypeToAvailableRooms = searchRoomTypeReservableQuantity(checkIn, checkOut);
        return roomTypeToAvailableRooms.entrySet()
                .stream()
                .map(keyValue -> new Pair<>(keyValue.getKey(), keyValue.getValue()))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    //Gives you the allocatable quantity of the room type 
    private Integer getAllocatableQuantityByRoomType(LocalDate checkIn, LocalDate checkOut, String roomTypeName) throws DoesNotExistException {
        RoomTypeEntity selectedRoomType = this.retrieveRoomTypeByName(roomTypeName);

        Set<RoomEntity> availableAndEnabledRooms = getAvailableAndEnabledRoomsByRoomType(selectedRoomType);

        boolean free;
        int roomsFree = 0;
        for (RoomEntity potentialFreeRoom : availableAndEnabledRooms) {
            //Room is not free if any of its RE coincides with guest's period of stay
            free = reservationSessionBean.checkRoomSchedule(potentialFreeRoom, checkIn, checkOut);
            if (free) {
                roomsFree++;
            }
        }

        return roomsFree;
    }

    @Override
    public Set<RoomEntity> getAvailableAndEnabledRoomsByRoomType(RoomTypeEntity selectedRoomType) {
        return selectedRoomType.getRoomEntities()
                .stream()
                .filter(room -> !room.getIsDisabled() && room.getRoomStatusEnum() == RoomStatusEnum.AVAILABLE)
                .collect(Collectors.toSet());
    }

    //TODO:Refactor later?
    private Optional<RoomEntity> getAllocatableRoomByRoomType(LocalDate checkIn, LocalDate checkOut, String roomTypeName) throws DoesNotExistException {
        RoomTypeEntity selectedRoomType = this.retrieveRoomTypeByName(roomTypeName);

        Set<RoomEntity> availableAndEnabledRooms = getAvailableAndEnabledRoomsByRoomType(selectedRoomType);

        boolean free;
        for (RoomEntity potentialFreeRoom : availableAndEnabledRooms) {
            //Room is not free if any of its RLE coincides with guest's period of stay
            free = reservationSessionBean.checkRoomSchedule(potentialFreeRoom, checkIn, checkOut);
            if (free) {
                return Optional.ofNullable(potentialFreeRoom);
            }
        }

        return Optional.empty();
    }

    private boolean isBeforeInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) <= 0;
    }

    private boolean isAfterInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) >= 0;
    }

    private Optional<String> getNextHigherRankRoomTypeOf(String currRoomType) {
        List<RoomTypeEntity> allRoomTypes = retrieveAllRoomTypes();
        Collections.sort(allRoomTypes);

        //The highest ranked was already booked
        if (allRoomTypes.get(allRoomTypes.size() - 1).getName().equals(currRoomType)) {
            return Optional.empty();
        }

        return Stream.iterate(0, index -> index + 1)
                .filter(index -> allRoomTypes.get(index).equals(currRoomType))
                .map(index -> allRoomTypes.get(index + 1).getName())
                .findFirst();
    }

    @Override
    public void allocateRoomsToCurrentDayReservations() throws DoesNotExistException {

        Set<ReservationEntity> currentDayReservations = reservationSessionBean.retrieveReservationByCheckIn(LocalDate.now());

        Set<ReservationEntity> reservations_unavailableRooms = currentDayReservations.stream()
                .filter(currRes -> {

                    if (currRes.getRoomEntity().getRoomStatusEnum() == RoomStatusEnum.AVAILABLE) {
                        currRes.setIsAllocated(true);
                    }

                    return currRes.getRoomEntity().getRoomStatusEnum() == RoomStatusEnum.UNAVAILABLE;
                })
                .collect(Collectors.toSet());

        Set<ReservationEntity> reservations_noFreeRooms = new HashSet<>(reservations_unavailableRooms);
        LocalDate checkIn, checkOut;
        for (ReservationEntity currRes : reservations_unavailableRooms) {

            checkIn = BossHelper.dateToLocalDate(currRes.getCheckInDate());
            checkOut = BossHelper.dateToLocalDate(currRes.getCheckOutDate());

            getAllocatableRoomByRoomType(checkIn, checkOut, currRes.getRoomTypeEntity().getName())
                    .ifPresent(potentialFreeRoom -> {
                        currRes.getRoomEntity().disassociateReservationEntities(currRes);
                        potentialFreeRoom.associateReservationEntities(currRes);
                        currRes.setIsAllocated(true);
                        reservations_noFreeRooms.remove(currRes);
                    });
        }

        //ATP, I have reservations that are not allocated despite searching for new rooms
        //Search higher room type for each reservation
        Map<String, Set<ReservationEntity>> roomTypeNameToReservations = reservations_noFreeRooms.stream()
                .collect(
                        Collectors.groupingBy(res -> res.getRoomTypeEntity().getName(), Collectors.toSet())
                );

    }

}
