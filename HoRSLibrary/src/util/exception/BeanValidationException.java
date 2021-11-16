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
public class BeanValidationException extends Exception{

    public BeanValidationException() {
    }

    public BeanValidationException(String string) {
        super(string);
    }
    
}
