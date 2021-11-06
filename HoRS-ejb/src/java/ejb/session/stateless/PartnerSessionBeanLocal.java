/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import entity.ReservationEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Local
public interface PartnerSessionBeanLocal {

    public PartnerEntity createNewPartner(PartnerEntity newPartnerEntity) throws AlreadyExistsException, UnknownPersistenceException;

    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginException;

    public PartnerEntity retrievePartnerByUsername(String username) throws DoesNotExistException;

    public ReservationEntity retrieveReservationsByPartner(String emailAddress, Long reservationId) throws DoesNotExistException;
    
    List<ReservationEntity> retrieveAllReservationsByPartner(String emailAddress) throws DoesNotExistException;

}
