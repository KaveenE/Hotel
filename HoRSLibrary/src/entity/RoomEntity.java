/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.RoomStatusEnum;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Entity
public class RoomEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, unique = true)
    private Long floorUnitNo;
    @Enumerated(EnumType.STRING)
    private RoomStatusEnum roomStatusEnum;
    @Column(nullable = false)
    private Boolean isDisabled;

    @JoinColumn(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RoomTypeEntity roomTypeEntity;
    @OneToMany(mappedBy = "roomEntity")
    private Set<ReservationEntity> reservationEntities;

    public RoomEntity() {
        this.reservationEntities = new HashSet<>();
        this.isDisabled = false;
        this.roomStatusEnum = RoomStatusEnum.AVAILABLE;
    }

    public RoomEntity(Long floorUnitNo, RoomTypeEntity roomTypeEntity) {
        this();
        this.floorUnitNo = floorUnitNo;
        this.roomTypeEntity = roomTypeEntity;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RoomEntity)) {
            return false;
        }
        RoomEntity other = (RoomEntity) object;
        if (this.getRoomId() == null && other.getRoomId() == null) {
            return false;
        }
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomEntity[ id=" + roomId + " ]";
    }

    public Long getFloorUnitNo() {
        return floorUnitNo;
    }

    public void setFloorUnitNo(Long floorUnitNo) {
        this.floorUnitNo = floorUnitNo;
    }

    public RoomTypeEntity getRoomTypeEntity() {
        return roomTypeEntity;
    }

    public void setRoomTypeEntity(RoomTypeEntity roomTypeEntity) {
        this.roomTypeEntity = roomTypeEntity;
    }

    public Set<ReservationEntity> getReservationEntities() {
        return reservationEntities;
    }

    public void associateReservationEntities(ReservationEntity... reservationEntities) {
        Set<ReservationEntity> reservationEntitySet = BossHelper.convertArrayToSet(reservationEntities);
        this.associateReservationEntities(reservationEntitySet);
    }

    public void associateReservationEntities(Collection<ReservationEntity> reservationEntities) {
        this.reservationEntities.addAll(reservationEntities);
        reservationEntities.forEach(reservationEntity -> reservationEntity.setRoomEntity(this));
    }

    public void disassociateReservationEntities(ReservationEntity... reservationEntities) {
        Set<ReservationEntity> reservationEntitySet = BossHelper.convertArrayToSet(reservationEntities);
        this.disassociateReservationEntities(reservationEntitySet);
    }

    //RoomEntity is compulsory for ReservationEntity, we may not have to disassociate as the only relevant delete UC is delete room where we can mark as disabled if not "used" or remove instead
    public void disassociateReservationEntities(Collection<ReservationEntity> reservationEntities) {
        //reservationEntities.forEach(reservationEntity -> reservationEntity.setRoomEntity(null));
        if (reservationEntities != this.reservationEntities) {
            this.reservationEntities.removeAll(reservationEntities);
        } else {
            this.reservationEntities = new HashSet<>();

        }
    }

    public RoomStatusEnum getRoomStatusEnum() {
        return roomStatusEnum;
    }

    public void setRoomStatusEnum(RoomStatusEnum roomStatusEnum) {
        this.roomStatusEnum = roomStatusEnum;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}
