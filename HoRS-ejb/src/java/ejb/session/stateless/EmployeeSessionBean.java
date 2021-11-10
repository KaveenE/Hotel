/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AlreadyExistsException;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;
import util.exception.EmployeeAlreadyExistsException;
import util.exception.EmployeeDoesNotExistException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public EmployeeEntity createNewEmployee(EmployeeEntity newEmployeeEntity) throws AlreadyExistsException, UnknownPersistenceException, BeanValidationException{
        BossHelper.throwValidationErrorsIfAny(newEmployeeEntity);
        try {
            em.persist(newEmployeeEntity);
            em.flush();

        } catch (PersistenceException ex) {
            AlreadyExistsException.throwAlreadyExistsOrUnknownException(ex, new EmployeeAlreadyExistsException());
        }
        return newEmployeeEntity;
    }

    @Override
    public List<EmployeeEntity> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM EmployeeEntity e");
        return query.getResultList();
    }

    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws DoesNotExistException {
        try {
            return (EmployeeEntity) em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.username = :username")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeDoesNotExistException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginException {

        try {
            EmployeeEntity employeeEntity = retrieveEmployeeByUsername(username);

            if (employeeEntity.getPassword().equals(password)) {
                return employeeEntity;
            } else {
                throw new InvalidLoginException("Username does not exist or invalid password!");
            }
        } catch (DoesNotExistException ex) {
            throw new InvalidLoginException("Username does not exist or invalid password!");
        }

    }
}
