/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.PeakRateEntity;
import entity.PromoRateEntity;

import entity.ReservationEntity;
import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.DoesNotExistException;
import util.exception.RoomRateDoesNotExistException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    private void createReservaton(Collection<ReservationEntity> reservations) {
        reservations.forEach(reservation -> em.persist(reservation));
    }

    @Override
    public ReservationEntity walkInReserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, Long roomQuantity) throws DoesNotExistException {
        return this.reserveRoomsByRoomType(reservation, roomTypeName, true, roomQuantity);
    }

    @Override
    public ReservationEntity reserveRoomsByRoomType(ReservationEntity reservation, String roomTypeName, boolean walkIn, Long roomQuantity) throws DoesNotExistException {
        RoomTypeEntity roomTypeToReserve = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
        roomTypeToReserve.associateReservationEntity(reservation);

        computeAndAssociatePriceOfReservation(roomTypeToReserve, reservation, walkIn);

        LocalDateTime checkInDateTime = BossHelper.dateToLocalDateTime(reservation.getCheckInDate());
        if (checkInDateTime.toLocalDate().equals(LocalDate.now()) && checkInDateTime.getHour() > 2) {
            //TODO: Same day reservation, have to allocate

        }

        Set<ReservationEntity> reservations = Stream.generate(() -> new ReservationEntity(reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getPriceOfStay()))
                .limit(roomQuantity)
                .collect(Collectors.toSet());
        
        this.createReservaton(reservations);
        
        return reservation;

    }

    private void computeAndAssociatePriceOfReservation(RoomTypeEntity roomTypeToReserve, ReservationEntity reservation, Boolean walkIn) throws DoesNotExistException {

        LocalDate checkIn = BossHelper.dateToLocalDate(reservation.getCheckInDate());
        LocalDate checkOut = BossHelper.dateToLocalDate(reservation.getCheckOutDate());
        BigDecimal priceOfStay = BigDecimal.ZERO;

        if (walkIn) {
            RoomRateAbsEntity publishedRate = roomTypeToReserve.getPublishedRate()
                    .orElseThrow(() -> new RoomRateDoesNotExistException("Published rate is not linked with RoomType, " + roomTypeToReserve.getName()));
            publishedRate.associateReservations(reservation);

            priceOfStay = publishedRate.getRatePerNight()
                    .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(checkIn, checkOut)+1));
        } else {

            LocalDate counterCheckIn = checkIn;
            Map.Entry<RoomRateAbsEntity, Long> rateToLengthOfRate;

            while (isBeforeInclusive(counterCheckIn, checkOut)) {
                rateToLengthOfRate = getPriceRateForNight(counterCheckIn, checkOut, roomTypeToReserve);
                counterCheckIn = counterCheckIn.plusDays(rateToLengthOfRate.getValue());

                priceOfStay.add(rateToLengthOfRate.getKey().getRatePerNight()
                        .multiply(BigDecimal.valueOf(rateToLengthOfRate.getValue()))
                );
            }

        }

        reservation.setPriceOfStay(priceOfStay);

    }

    /**
     * Implementation Details: We check for <strong>Promo>Peak>Normal</strong>
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

}
