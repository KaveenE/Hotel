/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GuestEntity;
import entity.GuestReservationEntity;
import entity.PartnerReservationEntity;
import entity.PeakRateEntity;
import entity.PromoRateEntity;

import entity.ReservationEntity;
import entity.RoomEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.RoomStatusEnum;
import util.exception.DoesNotExistException;
import util.exception.ReservationDoesNotExistException;
import util.exception.RoomRateDoesNotExistException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    @EJB
    private GuestSessionBeanLocal guestSessionBean;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    private void createReservaton(Collection<ReservationEntity> reservations) {
        reservations.forEach(reservation -> em.persist(reservation));
        em.flush();
    }

    @Override
    public ReservationEntity retrieveReservationById(Long resId) throws DoesNotExistException {
        return BossHelper.requireNonNull(em.find(ReservationEntity.class, resId), new ReservationDoesNotExistException());
    }

    //returns a Set of reservation entities with same check in date as input check in date
    @Override
    public Set<ReservationEntity> retrieveReservationByCheckIn(LocalDate checkIn) {
        return em.createQuery("SELECT res FROM ReservationEntity res", ReservationEntity.class)
                .getResultList()
                .stream()
                .filter(res -> BossHelper.dateToLocalDate(res.getCheckInDate()).isEqual(checkIn))
                .collect(Collectors.toSet());
    }

    //returns a list of guest's reservation entites with same check in date as input check in date
    @Override
    public Set<ReservationEntity> retrieveReservationByCheckInAndGuest(LocalDate checkIn, String username) throws DoesNotExistException {
        Set<ReservationEntity> allReservationsByGuest = guestSessionBean.retrieveAllReservationsByGuest(username);

        return allReservationsByGuest.stream()
                .filter(res -> BossHelper.dateToLocalDate(res.getCheckInDate())
                .equals(checkIn))
                .collect(Collectors.toSet());
    }

    //overloaded method to facilitate room reservations for walk in only
    @Override
    public void walkInReserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity) throws DoesNotExistException {
        this.reserveRoomsByRoomType(reservation, roomTypeName, roomQuantity, null);

    }

    //creates and associates reservations with room rate, room type and rooms
    @Override
    public void reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity, String username) throws DoesNotExistException {
        boolean walkIn = !reservation.getOnline();
        Stream<ReservationEntity> reservationStream;

        if (reservation instanceof GuestReservationEntity) {
            reservationStream = Stream.generate(() -> new GuestReservationEntity(reservation.getCheckInDate(), reservation.getCheckOutDate()));
        } else if (reservation instanceof PartnerReservationEntity) {
            reservationStream = Stream.generate(() -> new PartnerReservationEntity(reservation.getCheckInDate(), reservation.getCheckOutDate()));
        } else {
            reservationStream = Stream.generate(() -> new ReservationEntity(reservation.getCheckInDate(), reservation.getCheckOutDate()));
        }

        Set<ReservationEntity> reservations = reservationStream.limit(roomQuantity)
                .collect(Collectors.toSet());

        //Associate RT
        RoomTypeEntity roomTypeToReserve = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
        roomTypeToReserve.associateReservationEntity(reservations);

        //Associate RR
        computeAndAssociatePriceOfReservation(roomTypeToReserve, reservations, walkIn);

        //Associate Rooms
        associateToPotentialFreeRooms(reservations, roomTypeName);

        LocalDateTime checkInDateTime = BossHelper.dateToLocalDateTime(reservation.getCheckInDate());
        if (checkInDateTime.toLocalDate().equals(LocalDate.now()) && checkInDateTime.getHour() > 2) {
            //TODO: Same day reservation, have to allocate
        }

        if (!walkIn) {
            if (reservation instanceof GuestReservationEntity) {
                GuestEntity guestEntity = guestSessionBean.retrieveGuestByUsername(username);
                guestEntity.associateGuestReservationEntities(reservations
                        .stream()
                        .map(res -> (GuestReservationEntity) res)
                        .collect(Collectors.toSet()));
            } else {
                partnerSessionBean.retrievePartnerByUsername(username)
                        .associatePartnerReservationEntities(reservations
                                .stream()
                                .map(res -> (PartnerReservationEntity) res)
                                .collect(Collectors.toSet()));
            }
        }

        this.createReservaton(reservations);

    }

    //helper method of reserveRoomsByRoomType, sets the price for price of stay and associates reservation with room rate
    private void computeAndAssociatePriceOfReservation(RoomTypeEntity roomTypeToReserve, Set<ReservationEntity> reservations, Boolean walkIn) throws DoesNotExistException {
        ReservationEntity reservation = reservations.iterator().next();
        LocalDate checkIn = BossHelper.dateToLocalDate(reservation.getCheckInDate());
        LocalDate checkOut = BossHelper.dateToLocalDate(reservation.getCheckOutDate());
        BigDecimal priceOfStay = BigDecimal.ZERO;

        if (walkIn) {
            RoomRateAbsEntity publishedRate = roomTypeToReserve.getPublishedRate()
                    .orElseThrow(() -> new RoomRateDoesNotExistException("Published rate is not linked with RoomType, " + roomTypeToReserve.getName()));
            publishedRate.associateReservations(reservations);

            priceOfStay = publishedRate.getRatePerNight()
                    .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(checkIn, checkOut) + 1));
        } else {

            LocalDate counterCheckIn = checkIn;
            Map.Entry<RoomRateAbsEntity, Long> rateToLengthOfRate;

            while (isBeforeInclusive(counterCheckIn, checkOut)) {
                rateToLengthOfRate = getPriceRateForNight(counterCheckIn, checkOut, roomTypeToReserve);
                counterCheckIn = counterCheckIn.plusDays(rateToLengthOfRate.getValue());

                rateToLengthOfRate.getKey().associateReservations(reservations);

                priceOfStay = priceOfStay.add(rateToLengthOfRate.getKey().getRatePerNight()
                        .multiply(BigDecimal.valueOf(rateToLengthOfRate.getValue()))
                );
            }

        }

        for (ReservationEntity re : reservations) {
            re.setPriceOfStay(priceOfStay);
        }

    }

    //helper method of reserveRoomsByRoomType, associates every reservation entity with a allocatable room
    private void associateToPotentialFreeRooms(Set<ReservationEntity> reservations, String roomTypeName) throws DoesNotExistException {
        Queue<ReservationEntity> reservationQueue = new ArrayDeque<>(reservations);

        ReservationEntity reservation = reservations.iterator().next();
        LocalDate checkIn = BossHelper.dateToLocalDate(reservation.getCheckInDate());
        LocalDate checkOut = BossHelper.dateToLocalDate(reservation.getCheckOutDate());

        RoomTypeEntity selectedRoomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);

        Set<RoomEntity> availableAndEnabledRooms = roomTypeSessionBean.getAvailableAndEnabledRoomsByRoomType(selectedRoomType);

        boolean free;
        for (RoomEntity potentialFreeRoom : availableAndEnabledRooms) {
            //Room is not free if any of its RLE coincides with guest's period of stay
            free = checkRoomSchedule(potentialFreeRoom, checkIn, checkOut);
            if (free && !reservationQueue.isEmpty()) {
                potentialFreeRoom.associateReservationEntities(reservationQueue.poll());
            }

        }
    }

    @Override
    public Boolean checkRoomSchedule(RoomEntity potentialFreeRoom, LocalDate checkIn, LocalDate checkOut) {
        return potentialFreeRoom.getReservationEntities()
                .stream()
                .allMatch(res -> isBeforeInclusive(res.getCheckOutDate(), checkIn) || isAfterInclusive(res.getCheckInDate(), checkOut));
    }

    /**
     * Implementation Details: We check for
     * <strong>Promo>Peak>Normal</strong>
     * in this order following their ranking.
     * <ul>
     * <li>This is so that I can <em>break</em> loop the moment Promo is
     * found.</li>
     * <li>This is why I made the RoomAbsRateEntity comparable</li>
     * </ul>
     * Everytime I check a rate, I attempt to find the longest days the rate is
     * applicable for
     * <ul>
     * <li>This is so that I don't have to iterate through every single day
     * (this happens in braindead version).</li>
     * </ul>
     * I store all the results in a TreeMap, from which I extract the largest
     * value after the loop has ended.
     *
     * @param currentDateInHotel The date at which he is staying in hotel
     * @param checkOut The date at which customer plans to checkout
     * @param roomTypeToReserve The roomType customer opted for
     * @return The best rate applicable and the duration(in days) you can apply
     * for
     */
    private Map.Entry<RoomRateAbsEntity, Long> getPriceRateForNight(LocalDate currentDateInHotel, LocalDate checkOut, RoomTypeEntity roomTypeToReserve) {
        PromoRateEntity promoRate;
        PeakRateEntity peakRate;
        LocalDate validFrom, validTo;
        TreeMap<RoomRateAbsEntity, Long> rateTolengthOfRate = new TreeMap<>();

        for (RoomRateAbsEntity roomRate : roomTypeToReserve.getRoomRateAbsEntities()) {

            if (roomRate instanceof PromoRateEntity) {
                promoRate = (PromoRateEntity) roomRate;
                validFrom = BossHelper.dateToLocalDate(promoRate.getValidFrom());
                validTo = BossHelper.dateToLocalDate(promoRate.getValidTo());

                if (isAfterInclusive(currentDateInHotel, validFrom) && isBeforeInclusive(checkOut, validTo)) {
                    rateTolengthOfRate.put(promoRate, ChronoUnit.DAYS.between(currentDateInHotel, checkOut) + 1);
                } else if (isAfterInclusive(currentDateInHotel, validFrom) && checkOut.isAfter(validTo)) {
                    rateTolengthOfRate.put(promoRate, ChronoUnit.DAYS.between(currentDateInHotel, validTo) + 1);
                }

                break;

            } else if (roomRate instanceof PeakRateEntity) {
                peakRate = (PeakRateEntity) roomRate;
                validFrom = BossHelper.dateToLocalDate(peakRate.getValidFrom());
                validTo = BossHelper.dateToLocalDate(peakRate.getValidTo());

                if (isAfterInclusive(currentDateInHotel, validFrom) && isBeforeInclusive(checkOut, validTo)) {
                    rateTolengthOfRate.put(peakRate, ChronoUnit.DAYS.between(currentDateInHotel, checkOut) + 1);
                } else if (isAfterInclusive(currentDateInHotel, validFrom) && checkOut.isAfter(validTo)) {
                    rateTolengthOfRate.put(peakRate, ChronoUnit.DAYS.between(currentDateInHotel, validTo) + 1);
                }

            } else {
                rateTolengthOfRate.put(roomRate, 1L);
            }
        }

        return rateTolengthOfRate.lastEntry();
    }

    private boolean isBeforeInclusive(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.compareTo(checkOut) <= 0;
    }

    private boolean isAfterInclusive(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.compareTo(checkOut) >= 0;
    }

    private boolean isBeforeInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) <= 0;
    }

    private boolean isAfterInclusive(Date basisDate, LocalDate otherDate) {
        return BossHelper.dateToLocalDate(basisDate).compareTo(otherDate) >= 0;
    }

}
