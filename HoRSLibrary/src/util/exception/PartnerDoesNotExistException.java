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
public class PartnerDoesNotExistException extends DoesNotExistException {

    public PartnerDoesNotExistException() {
        this("Partner does not exist!");
    }

    public PartnerDoesNotExistException(String string) {
        super(string);
    }

}
