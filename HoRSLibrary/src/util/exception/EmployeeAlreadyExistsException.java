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
public class EmployeeAlreadyExistsException extends AlreadyExistsException {

    public EmployeeAlreadyExistsException() {
        this("Employee already exists either with same username or primary key!");
    }

    public EmployeeAlreadyExistsException(String msg) {
        super(msg);
    }

}
