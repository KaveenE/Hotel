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
public class InvalidAccessRightException extends Exception {

    public InvalidAccessRightException() {
        this("Invalid access rights!");

    }

    public InvalidAccessRightException(String string) {
        super(string);
    }

}
