/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;

/**
 *
 * @author PP42
 */
@Entity
public class NormalRateEntity extends RoomRateAbsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public NormalRateEntity() {
    }

    public NormalRateEntity(String name, BigDecimal ratePerNight) {
        super(name, ratePerNight);
    }

    @Override
    public String toString() {
        return "entity.NormalRateEntity[ id=" + super.getRoomRateId() + " ]";
    }

}
