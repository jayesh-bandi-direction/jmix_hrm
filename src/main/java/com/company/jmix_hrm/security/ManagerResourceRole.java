package com.company.jmix_hrm.security;

import com.company.jmix_hrm.dto.EmployeeDto;
import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

// Created the resource role for manager, so that manager can access the employee and department entity, view and perform crud operation
@ResourceRole(
        name = "Manager: CRUD Employee/Department",
        code = ManagerResourceRole.CODE,
        scope = SecurityScope.UI // UI - used to specify what user can see and do on when interacting with application on their browser
)
public interface ManagerResourceRole {

    String CODE = "manager-resource-role";

    @MenuPolicy(menuIds = {"userMenu", "departmentMenu"})
    @ViewPolicy(viewIds = {"User.list", "User.detail", "Employee.list", "Department.list", "Department.detail", "EmployeeDto.list"})
    void managerScreen();

    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.ALL)
    @EntityPolicy(entityClass = Employee.class, actions = EntityPolicyAction.ALL)
    @EntityPolicy(entityClass = Department.class, actions = EntityPolicyAction.ALL)
    @EntityPolicy(entityClass = Company.class, actions = EntityPolicyAction.READ)
    @EntityPolicy(entityClass = EmployeeDto.class, actions = EntityPolicyAction.ALL)
//    If we don't specify the attribute policy then even though we have access to entity we will not be able to see the records
//    @EntityAttributePolicy(entityClass = User.class, attributes = {"active", "employee"}, action = EntityAttributePolicyAction.MODIFY)
//    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "firstName", "lastName", "email", "company"}, action = EntityAttributePolicyAction.VIEW)
//    @EntityAttributePolicy(entityClass = Employee.class, attributes = {"designation", "manager", "department"}, action = EntityAttributePolicyAction.MODIFY)
//    @EntityAttributePolicy(entityClass = Employee.class, attributes = {"dateOfBirth", "gender", "employeeCode"}, action = EntityAttributePolicyAction.VIEW)
    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Employee.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Department.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Company.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityAttributePolicy(entityClass = EmployeeDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void managerEntity();

}
