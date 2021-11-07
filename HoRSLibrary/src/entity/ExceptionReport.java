/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author enkav
 */
@Embeddable
public class ExceptionReport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer exceptionType;
    private String messageToHotel;
    private String messageToGuest;

    public ExceptionReport() {
    }

    public ExceptionReport(Integer exceptionType, String messageToHotel, String messageToGuest) {
        this.exceptionType = exceptionType;
        this.messageToHotel = messageToHotel;
        this.messageToGuest = messageToGuest;
    }

    public Integer getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getMessageToHotel() {
        return messageToHotel;
    }

    public void setMessageToHotel(String messageToHotel) {
        this.messageToHotel = messageToHotel;
    }

    public String getMessageToGuest() {
        return messageToGuest;
    }

    public void setMessageToGuest(String messageToGuest) {
        this.messageToGuest = messageToGuest;
    }

}
