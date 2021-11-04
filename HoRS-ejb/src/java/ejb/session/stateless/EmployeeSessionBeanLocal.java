/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.DoesNotExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author PP42
 */
@Local
public interface EmployeeSessionBeanLocal {

    public EmployeeEntity retrieveEmployeeByUsername(String username) throws DoesNotExistException;

    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws AlreadyExistsException, UnknownPersistenceException;

}
