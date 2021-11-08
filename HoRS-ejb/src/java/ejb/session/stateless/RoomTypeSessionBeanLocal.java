/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomEntity;
import entity.RoomTypeEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;
import util.helper.Pair;

/**
 *
 * @author enkav
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomTypeEntity retrieveRoomTypeById(Long roomTypeId) throws DoesNotExistException;

    public RoomTypeEntity createRoomType(RoomTypeEntity roomType) throws DoesNotExistException, UnknownPersistenceException, AlreadyExistsException;

    public Map<String, Integer> walkInSearchRoomTypeReservableQuantity(LocalDate checkOut) throws DoesNotExistException;

    public Map<String, Integer> searchRoomTypeReservableQuantity(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException;

    public RoomTypeEntity retrieveRoomTypeByName(String name) throws DoesNotExistException;
    
    public List<Pair<String, Integer>> searchRoomTypeReservableQuantityForPartner(LocalDate checkIn, LocalDate checkOut) throws DoesNotExistException;
    
    public Set<RoomEntity> getAvailableAndEnabledRoomsByRoomType(RoomTypeEntity selectedRoomType);

}
