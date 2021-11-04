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
public class EmployeeDoesNotExistException extends DoesNotExistException {

    public EmployeeDoesNotExistException() {
        this("Employee does not exist!");
    }

    public EmployeeDoesNotExistException(String string) {
        super(string);
    }

}
