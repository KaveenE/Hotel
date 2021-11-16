/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Entity
public class RoomTypeEntity implements Serializable, Comparable<RoomTypeEntity> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    @Embedded
    @NotNull
    private RoomConfig roomConfig;

    @ElementCollection
    @Column(nullable = false)
    @NotNull
    private List<String> amenities;

    @Column(nullable = false)
    @NotNull
    private Boolean isDisabled;

    @OneToMany(mappedBy = "roomTypeEntity", cascade = CascadeType.ALL)
    @NotNull
    private Set<RoomRateAbsEntity> roomRateAbsEntities;

    @OneToMany(mappedBy = "roomTypeEntity", cascade = CascadeType.ALL)
    @NotNull
    private Set<RoomEntity> roomEntities;

    @OneToMany(mappedBy = "roomTypeEntity", cascade = CascadeType.ALL)
    @NotNull
    private Set<ReservationEntity> reservationEntities;

    public RoomTypeEntity() {
        this.roomRateAbsEntities = new HashSet<>();
        this.roomEntities = new HashSet<>();
        this.reservationEntities = new HashSet<>();
        this.roomConfig = new RoomConfig();
        this.isDisabled = false;
    }

    public RoomTypeEntity(RoomConfig roomConfig, List<String> amenities) {
        this();
        this.roomConfig = roomConfig;
        this.amenities = amenities;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RoomTypeEntity)) {
            return false;
        }
        RoomTypeEntity other = (RoomTypeEntity) object;
        if (this.roomTypeId == null && other.roomTypeId == null) {
            return false;
        }
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomTypeEntity[ id=" + roomTypeId + " ]";
    }

    public RoomConfig getRoomConfig() {
        return roomConfig;
    }

    public void setRoomConfig(RoomConfig roomConfig) {
        this.roomConfig = roomConfig;
    }

    public String getName() {
        return roomConfig.getName();
    }

    public void setName(String name) {
        this.roomConfig.setName(name);
    }

    public String getDescription() {
        return roomConfig.getDescription();
    }

    public void setDescription(String description) {
        this.roomConfig.setDescription(description);
    }

    public Long getMySize() {
        return roomConfig.getMySize();
    }

    public void setMySize(Long mySize) {
        this.roomConfig.setMySize(mySize);
    }

    public String getBed() {
        return roomConfig.getBed();
    }

    public void setBed(String bed) {
        this.roomConfig.setBed(bed);
    }

    public Integer getCapacity() {
        return roomConfig.getCapacity();
    }

    public void setCapacity(Integer capacity) {
        this.roomConfig.setCapacity(capacity);
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public Set<RoomRateAbsEntity> getRoomRateAbsEntities() {
        return roomRateAbsEntities;
    }

    public Integer getRanking() {
        return roomConfig.getRanking();
    }

    public void setRanking(Integer ranking) {
        roomConfig.setRanking(ranking);
    }

    public void associateRoomRateAbsEntity(RoomRateAbsEntity... roomRateAbsEntities) {
        Set<RoomRateAbsEntity> roomRateAbsEntitySet = BossHelper.convertArrayToSet(roomRateAbsEntities);
        this.associateRoomRateAbsEntity(roomRateAbsEntitySet);
    }

    public void associateRoomRateAbsEntity(Collection<RoomRateAbsEntity> roomRateAbsEntities) {
        this.roomRateAbsEntities.addAll(roomRateAbsEntities);
        roomRateAbsEntities.forEach(rrEntity -> rrEntity.setRoomTypeEntity(this));
    }

    public void disassociateRoomRateAbsEntity(RoomRateAbsEntity... roomRateAbsEntities) {
        Set<RoomRateAbsEntity> roomRateAbsEntitySet = BossHelper.convertArrayToSet(roomRateAbsEntities);
        this.disassociateRoomRateAbsEntity(roomRateAbsEntitySet);
    }

    public void disassociateRoomRateAbsEntity(Collection<RoomRateAbsEntity> roomRateAbsEntities) {
        if (roomRateAbsEntities != this.roomRateAbsEntities) {
            this.roomRateAbsEntities.removeAll(roomRateAbsEntities);
        } else {
            this.roomRateAbsEntities = new HashSet<>();
        }

    }

    public Set<ReservationEntity> getReservationEntities() {
        return reservationEntities;
    }

    public Optional<RoomRateAbsEntity> getPublishedRate() {

        return roomRateAbsEntities.stream()
                .filter(potentialPublishedRate -> potentialPublishedRate instanceof PublishedRateEntity)
                .findFirst();

    }

    public void associateReservationEntity(ReservationEntity... reservationEntities) {
        Set<ReservationEntity> reservationEntitySet = BossHelper.convertArrayToSet(reservationEntities);
        this.associateReservationEntity(reservationEntitySet);
    }

    public void associateReservationEntity(Collection<ReservationEntity> reservationEntities) {
        this.reservationEntities.addAll(reservationEntities);
        reservationEntities.forEach(reservation -> reservation.setRoomTypeEntity(this));
    }

    public void disassociateReservationEntity(ReservationEntity... reservationEntities) {
        Set<ReservationEntity> reservationEntitySet = BossHelper.convertArrayToSet(reservationEntities);
        this.disassociateReservationEntity(reservationEntitySet);
    }

    public void disassociateReservationEntity(Collection<ReservationEntity> reservationEntities) {
        if (reservationEntities != this.reservationEntities) {
            this.reservationEntities.removeAll(reservationEntities);
        } else {
            this.reservationEntities = new HashSet<>();
        }
    }

    public Set<RoomEntity> getRoomEntities() {
        return roomEntities;
    }

    public void associateRoomEntities(RoomEntity... roomEntities) {
        Set<RoomEntity> roomEntitySet = BossHelper.convertArrayToSet(roomEntities);
        this.associateRoomEntities(roomEntitySet);
    }

    public void associateRoomEntities(Collection<RoomEntity> roomEntities) {
        this.roomEntities.addAll(roomEntities);
        roomEntities.forEach(roomEntity -> roomEntity.setRoomTypeEntity(this));
    }

    public void disassociateRoomEntities(RoomEntity... roomEntities) {
        Set<RoomEntity> roomEntitySet = BossHelper.convertArrayToSet(roomEntities);
        this.disassociateRoomEntities(roomEntitySet);
    }

    //RoomType mandatory for RoomEntity
    public void disassociateRoomEntities(Collection<RoomEntity> roomEntities) {
        //roomEntities.forEach(roomEntity -> roomEntity.setRoomTypeEntity(null));
        if (roomEntities != this.roomEntities) {
            this.roomEntities.removeAll(roomEntities);
        } else {
            this.roomEntities = new HashSet<>();
        }

    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    @Override
    public int compareTo(RoomTypeEntity o) {
        int compareRanks = roomConfig.compareTo(o.roomConfig);

        if (compareRanks == 0) {
            return (int) (o.getRoomTypeId() - this.getRoomTypeId());
        }

        return compareRanks;
    }

}
