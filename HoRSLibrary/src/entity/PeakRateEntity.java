/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author PP42
 */
@Entity
public class PeakRateEntity extends RoomRateAbsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @NotNull
    private Date validFrom;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @NotNull
    private Date validTo;

    public PeakRateEntity() {
    }

    public PeakRateEntity(Date validFrom, Date validTo, String name, BigDecimal ratePerNight, Boolean isUsed) {
        super(name, ratePerNight);
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        return "entity.PeakRateEntity[ id=" + super.getRoomRateId() + " ]";
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

}
