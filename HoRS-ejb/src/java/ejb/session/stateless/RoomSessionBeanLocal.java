/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Local
public interface RoomSessionBeanLocal {

    public RoomEntity retrieveRoomById(Long roomId) throws DoesNotExistException;

    public RoomEntity createRoomWithExistingRoomType(RoomEntity roomEntity, Long roomTypeId) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException, BeanValidationException;

}
