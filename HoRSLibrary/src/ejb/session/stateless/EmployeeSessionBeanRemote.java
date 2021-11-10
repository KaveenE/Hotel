/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author SCXY
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws AlreadyExistsException, UnknownPersistenceException, BeanValidationException;

    public List<EmployeeEntity> retrieveAllEmployees();

    public EmployeeEntity retrieveEmployeeByUsername(String username) throws DoesNotExistException;

    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginException;

}
