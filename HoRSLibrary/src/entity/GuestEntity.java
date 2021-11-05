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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Entity
public class GuestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;
    
    @Column(nullable = false, length = BossHelper.NAME_LENGTH, unique = true)
    private String emailAddress;
    @Column(nullable = false, length = BossHelper.PASSWORD_LENGTH)
    private String password;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "GUEST_ENTITY_FK")
    private Set<GuestReservationEntity> guestReservationEntities;
    
    public GuestEntity() {
        this.guestReservationEntities = new HashSet<>();
    }
    
    public GuestEntity(String emailAddress, String password) {
        this();
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guestId != null ? guestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof GuestEntity)) {
            return false;
        }
        GuestEntity other = (GuestEntity) object;
        if ((this.guestId == null && other.guestId != null) || (this.guestId != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.GuestEntity[ id=" + guestId + " ]";
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void associateGuestReservationEntities(GuestReservationEntity... guestReservationEntities) {
        Set<GuestReservationEntity> guestReservationEntitySet = BossHelper.convertArrayToSet(guestReservationEntities);
        this.associateGuestReservationEntities(guestReservationEntitySet);
    }

    public void associateGuestReservationEntities(Collection<GuestReservationEntity> guestReservationEntities) {
        this.guestReservationEntities.addAll(guestReservationEntities);
    }

    public void disassociateGuestReservationEntities(GuestReservationEntity... guestReservationEntities) {
        Set<GuestReservationEntity> guestReservationEntitySet = BossHelper.convertArrayToSet(guestReservationEntities);
        this.disassociateGuestReservationEntities(guestReservationEntitySet);
    }

    public void disassociateGuestReservationEntities(Collection<GuestReservationEntity> guestReservationEntities) {
        if (guestReservationEntities != this.guestReservationEntities) {
            this.guestReservationEntities.removeAll(guestReservationEntities);
        } else {
            this.guestReservationEntities = new HashSet<>();
        }

    }

    public Set<GuestReservationEntity> getGuestReservationEntities() {
        return guestReservationEntities;
    }

}
