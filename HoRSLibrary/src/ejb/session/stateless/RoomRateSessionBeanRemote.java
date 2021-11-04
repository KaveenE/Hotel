/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRateAbsEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DoesNotExistException;

/**
 *
 * @author enkav
 */
@Remote
public interface RoomRateSessionBeanRemote {

    public List<RoomRateAbsEntity> retrieveAllRoomRates();

    public RoomRateAbsEntity createRoomRateWithExistingRoomType(RoomRateAbsEntity roomRate, Long roomTypeId) throws DoesNotExistException;

    public void updateRoomRate(RoomRateAbsEntity roomRate) throws DoesNotExistException;

    public void deleteRoomRateById(Long id) throws DoesNotExistException;

    public RoomRateAbsEntity retrieveRoomRateById(Long roomRateId) throws DoesNotExistException;

    public List<RoomRateAbsEntity> retrieveRoomRatesBySubclass(Class subclass) throws DoesNotExistException;

}
