/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author PP42
 */
public class ReservationDoesNotExistException extends DoesNotExistException{

    public ReservationDoesNotExistException() {
        this("Reservation does not exist. Bad primary key!");
    }

    public ReservationDoesNotExistException(String string) {
        super(string);
    }
    
    
}
