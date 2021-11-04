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
public class RoomTypeAlreadyExistsException extends AlreadyExistsException {

    public RoomTypeAlreadyExistsException() {
        this("Room Type with either the same primary key or name already exists!");
    }

    public RoomTypeAlreadyExistsException(String msg) {
        super(msg);
    }

}
