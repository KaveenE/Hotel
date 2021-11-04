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
public class PartnerAlreadyExistsException extends AlreadyExistsException {

    public PartnerAlreadyExistsException() {
        this("Partner already exists either with same username or primary key!");
    }

    public PartnerAlreadyExistsException(String msg) {
        super(msg);
    }

}
