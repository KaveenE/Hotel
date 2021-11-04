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
public class RoomTypeDoesNotExistException extends DoesNotExistException {

    public RoomTypeDoesNotExistException() {
        this("This room type does not exist!");
    }

    public RoomTypeDoesNotExistException(String string) {
        super(string);
    }

}
