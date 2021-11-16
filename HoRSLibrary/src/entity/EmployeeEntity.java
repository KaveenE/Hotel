/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.EmployeeRoleEnum;
import util.helper.BossHelper;

/**
 *
 * @author PP42
 */
@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmployeeRoleEnum employeeRoleEnum;

    @Column(nullable = false, length = BossHelper.NAME_LENGTH, unique = true)
    @NotNull
    @Size(min = 5, max = BossHelper.NAME_LENGTH)
    private String username;

    @Column(nullable = false, length = BossHelper.PASSWORD_LENGTH)
    @NotNull
    @Size(min = 5, max = BossHelper.PASSWORD_LENGTH)
    private String password;

    public Long getEmployeeId() {
        return employeeId;
    }

    public EmployeeEntity() {
    }

    public EmployeeEntity(EmployeeRoleEnum employeeRoleEnum, String username, String password) {
        this.employeeRoleEnum = employeeRoleEnum;
        this.username = username;
        this.password = password;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public EmployeeRoleEnum getEmployeeRoleEnum() {
        return employeeRoleEnum;
    }

    public void setEmployeeRoleEnum(EmployeeRoleEnum employeeRoleEnum) {
        this.employeeRoleEnum = employeeRoleEnum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + employeeId + " ]";
    }

}
