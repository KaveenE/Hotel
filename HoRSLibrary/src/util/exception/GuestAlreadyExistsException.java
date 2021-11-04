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
public class GuestAlreadyExistsException extends AlreadyExistsException {

    public GuestAlreadyExistsException() {
        this("Guest already exists either with same username, mobile number or primary key!");
    }

    public GuestAlreadyExistsException(String msg) {
        super(msg);
    }

}
