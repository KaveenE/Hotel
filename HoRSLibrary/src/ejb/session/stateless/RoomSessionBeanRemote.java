/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author enkav
 */
@Remote
public interface RoomSessionBeanRemote {

    public RoomEntity createRoomWithExistingRoomType(RoomEntity roomEntity, Long roomTypeId) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException, BeanValidationException;

    public List<RoomEntity> retrieveAllRooms();

    public void updateRoom(RoomEntity room) throws DoesNotExistException, AlreadyExistsException, BeanValidationException;

    public void deleteRoomByFloorUnitNo(String floorUnitNo) throws DoesNotExistException;

    public RoomEntity retrieveRoomByFloorUnitNo(String floorUnitNo) throws DoesNotExistException;

}
