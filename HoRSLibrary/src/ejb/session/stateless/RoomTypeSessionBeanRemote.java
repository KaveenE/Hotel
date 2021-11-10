/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRateAbsEntity;
import entity.RoomTypeEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
public interface RoomTypeSessionBeanRemote {

    public RoomTypeEntity createRoomType(RoomTypeEntity roomType) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException,BeanValidationException;

    public List<RoomTypeEntity> retrieveAllRoomTypes();

    public RoomTypeEntity retrieveRoomTypeByName(String name) throws DoesNotExistException;

    public void deleteRoomTypeByName(String name) throws DoesNotExistException;

    public void updateRoomType(RoomTypeEntity roomType) throws DoesNotExistException, AlreadyExistsException,BeanValidationException;

    public Map<String, Integer> searchRoomTypeReservableQuantity(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException;

    public RoomRateAbsEntity retrieveRoomRateFromRoomType(String roomTypeName, Class subclass) throws DoesNotExistException;

    public Map<String, Integer> walkInSearchRoomTypeReservableQuantity(LocalDate checkOut) throws DoesNotExistException;
    
    public void allocateRoomsToCurrentDayReservations() throws DoesNotExistException;

    public void allocateRoomsToFutureReservations(LocalDate futureCheckIn) throws DoesNotExistException;

}
