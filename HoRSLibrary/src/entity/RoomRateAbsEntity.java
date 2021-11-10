/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RoomRateAbsEntity implements Serializable, Comparable<RoomRateAbsEntity>{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;

    @Column(nullable = false, length = BossHelper.NAME_LENGTH)
    @NotNull
    @Size(min =4 , max = BossHelper.NAME_LENGTH)
    private String name;
    @Column(nullable = false, scale = 2, precision = 8)
    @Digits(fraction = 2,integer = 8)
    @PositiveOrZero
    private BigDecimal ratePerNight;
    @Column(nullable = false)
    @NotNull
    private Boolean isDisabled;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull
    private RoomTypeEntity roomTypeEntity;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH})
    @NotEmpty
    private Set<ReservationEntity> reservations;

    public RoomRateAbsEntity() {
        this.isDisabled = false;
    }

    public RoomRateAbsEntity(String name, BigDecimal ratePerNight) {
        this();
        this.name = name;
        this.ratePerNight = ratePerNight;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RoomRateAbsEntity)) {
            return false;
        }
        RoomRateAbsEntity other = (RoomRateAbsEntity) object;
        if (this.roomRateId == null && other.roomRateId == null) {
            return false;
        }
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRateEntity[ id=" + roomRateId + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public RoomTypeEntity getRoomTypeEntity() {
        return roomTypeEntity;
    }

    public void setRoomTypeEntity(RoomTypeEntity roomTypeEntity) {
        this.roomTypeEntity = roomTypeEntity;
    }

    public Set<ReservationEntity> getReservations() {
        return reservations;
    }

    public void associateReservations(ReservationEntity... reservations) {
        Set<ReservationEntity> reservationSet = BossHelper.convertArrayToSet(reservations);
        this.associateReservations(reservationSet);
    }

    public void associateReservations(Collection<ReservationEntity> reservationSet) {
        this.reservations.addAll(reservationSet);
    }

    public void disassociateReservations(ReservationEntity... reservations) {
        Set<ReservationEntity> reservationSet = BossHelper.convertArrayToSet(reservations);
        this.disassociateReservations(reservationSet);
    }

    public void disassociateReservations(Collection<ReservationEntity> reservationSet) {
        
        if (reservationSet != this.reservations) {
            this.reservations.removeAll(reservationSet);
        } else {
            this.reservations = new HashSet<>();

        }
    }

    @Override
    public int compareTo(RoomRateAbsEntity o) {
        if(this.getClass() == o.getClass()) {
            return 0;
        }
        else if(this.getClass() == PromoRateEntity.class && o.getClass() != PromoRateEntity.class )
        {
            return 1;
        }
        else if(this.getClass() == PeakRateEntity.class && (o.getClass() == NormalRateEntity.class || o.getClass() == PublishedRateEntity.class) )
        {
            return 1;
        }
        else if(o.getClass() == NormalRateEntity.class && o.getClass() == PublishedRateEntity.class){
            return 1;
        }
        else {
            return -1;
        }
    }

}
