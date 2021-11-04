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
public class GuestDoesNotExistException extends DoesNotExistException {

    public GuestDoesNotExistException() {
        this("Guest does not exist!");

    }

    public GuestDoesNotExistException(String string) {
        super(string);
    }

}
