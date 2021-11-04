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
public class InvalidLoginException extends Exception {

    public InvalidLoginException() {
        this("Invalid login credentials!");
    }

    public InvalidLoginException(String string) {
        super(string);
    }

}
