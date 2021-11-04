/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author enkav
 */
public class RoomAlreadyExistsException extends AlreadyExistsException {

    public RoomAlreadyExistsException() {
        this("Room with either same primary key or floor unit number already exists!");
    }

    public RoomAlreadyExistsException(String msg) {
        super(msg);
    }

}
