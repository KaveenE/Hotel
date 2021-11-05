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
public class PartnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    @Column(nullable = false, length = BossHelper.NAME_LENGTH, unique = true)
    private String username;
    @Column(nullable = false, length = BossHelper.PASSWORD_LENGTH)
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARTNER_ENTITY_FK")
    private Set<ReservationEntity> partnerReservationEntities;

    public PartnerEntity() {
        this.partnerReservationEntities = new HashSet<>();
    }

    public PartnerEntity(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof PartnerEntity)) {
            return false;
        }
        PartnerEntity other = (PartnerEntity) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ReservationEntity> getPartnerReservationEntities() {
        return partnerReservationEntities;
    }

    public void associatePartnerReservationEntities(ReservationEntity... partnerReservationEntities) {
        Set<ReservationEntity> partnerReservationEntitySet = BossHelper.convertArrayToSet(partnerReservationEntities);
        this.associatePartnerReservationEntities(partnerReservationEntitySet);

    }

    public void associatePartnerReservationEntities(Collection<ReservationEntity> partnerReservationEntities) {
        this.partnerReservationEntities.addAll(partnerReservationEntities);

    }

    public void disassociatePartnerReservationEntities(ReservationEntity... partnerReservationEntities) {
        Set<ReservationEntity> partnerReservationEntitySet = BossHelper.convertArrayToSet(partnerReservationEntities);
        this.disassociatePartnerReservationEntities(partnerReservationEntitySet);

    }

    public void disassociatePartnerReservationEntities(Collection<ReservationEntity> partnerReservationEntities) {
        if (partnerReservationEntities != this.partnerReservationEntities) {
            this.partnerReservationEntities.removeAll(partnerReservationEntities);
        } else {
            this.partnerReservationEntities = new HashSet<>();
        }

    }

    @Override
    public String toString() {
        return "entity.PartnerEntity[ id=" + partnerId + " ]";
    }

}
