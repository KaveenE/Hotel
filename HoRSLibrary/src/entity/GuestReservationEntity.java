/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author PP42
 */
@Entity
public class GuestReservationEntity extends ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public GuestReservationEntity() {
    }

    public GuestReservationEntity(Date checkInDate, Date checkOutDate) {
        super(checkInDate, checkOutDate);
    }

    public GuestReservationEntity(Date checkInDate, Date checkOutDate, BigDecimal pricePaidForRLE) {
        super(checkInDate, checkOutDate, pricePaidForRLE);
    }

    @Override
    public String toString() {
        return "entity.GuestReservationEntity[ id=" + super.getReservationId() + " ]";
    }

}
