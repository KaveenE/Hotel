/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRateAbsEntity;
import javax.ejb.Local;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;

/**
 *
 * @author enkav
 */
@Local
public interface RoomRateSessionBeanLocal {

    public RoomRateAbsEntity retrieveRoomRateById(Long roomRateId) throws DoesNotExistException;

    public RoomRateAbsEntity createRoomRateWithExistingRoomType(RoomRateAbsEntity roomRate, Long roomTypeId) throws DoesNotExistException, BeanValidationException;
}
