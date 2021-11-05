/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PP42
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    //TODO: Do we need reserved data? No right?
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date checkInDate;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date checkOutDate;
    @Column(nullable = false, scale = 2, precision = 8)
    private BigDecimal priceOfStay;
    @Column(nullable = false)
    private Boolean online;
    private Boolean isAllocated;
    @Embedded
    private ExceptionReport exceptionReport;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private RoomEntity roomEntity;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RoomTypeEntity roomTypeEntity;

    public ReservationEntity() {
        online = (this instanceof PartnerReservationEntity || this instanceof GuestReservationEntity);
    }

    public ReservationEntity(Date checkInDate, Date checkOutDate) {
        this(checkInDate, checkOutDate, null);

    }
    
    public ReservationEntity(Date checkInDate, Date checkOutDate, BigDecimal priceOfStay) {
        this();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.priceOfStay = priceOfStay;

    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationId == null) && (other.reservationId == null)) {
            return false;
        }
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return String.format("%s of id %d with checkin date: %s and checkout date: %s was paid with %.2f",
                ReservationEntity.class.getSimpleName(), this.getReservationId(), sdf.format(checkInDate), sdf.format(checkOutDate), priceOfStay);
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public RoomTypeEntity getRoomTypeEntity() {
        return roomTypeEntity;
    }

    public void setRoomTypeEntity(RoomTypeEntity roomTypeEntity) {
        this.roomTypeEntity = roomTypeEntity;
    }

    public RoomEntity getRoomEntity() {
        return roomEntity;
    }

    public void setRoomEntity(RoomEntity roomEntity) {
        this.roomEntity = roomEntity;
    }

    public BigDecimal getPriceOfStay() {
        return priceOfStay;
    }

    public void setPriceOfStay(BigDecimal priceOfStay) {
        this.priceOfStay = priceOfStay;
    }

    public Boolean getIsAllocated() {
        return isAllocated;
    }

    public void setIsAllocated(Boolean isAllocated) {
        this.isAllocated = isAllocated;
    }

    public String getExceptionReport(boolean isHotel) {
        StringBuffer sb = new StringBuffer(exceptionReport.getExceptionType() + " ");
        
        if(isHotel) {
           return sb.append(exceptionReport.getMessageToHotel()).toString();
        }
        return sb.append(exceptionReport.getMessageToGuest()).toString();
    }

    public void setExceptionReport(ExceptionReport exceptionReport) {
        this.exceptionReport = exceptionReport;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

}
