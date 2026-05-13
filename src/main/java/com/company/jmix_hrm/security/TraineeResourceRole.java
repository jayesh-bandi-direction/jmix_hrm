package com.company.jmix_hrm.security;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.view.user.UserDetailView;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

// @ResourceRole - Used to specify that interface defines the resource role
//  1. name - attribute used to give name to the role
//  2. code - attribute used to give code to the role
//  3. scope - attribute used to specify that resource role is for giving UI access
@ResourceRole(name = "Trainee: View/Edit Employee", code = TraineeResourceRole.CODE, scope = SecurityScope.UI)
public interface TraineeResourceRole {

    //    specifying code
    String CODE = "trainee-resource-role";

    //    @ViewPolicy & @MenuPolicy are used to specify which screen and menu is accessible to the user
    @MenuPolicy(menuIds = "userMenu")
    @ViewPolicy(viewIds = "User.list")
    @ViewPolicy(viewClasses = UserDetailView.class)
    void screens();

    //    @EntityPolicy & @EntityAttributePolicy are used to specify which entity user can access and can modify
//    If we don't specify the @EntityAttributePolicy then user cannot see the records of the specified entity
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "firstName", "lastName", "email", "employee", "company"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Employee.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    @EntityAttributePolicy(entityClass = Employee.class, attributes = {"employeeCode", "gender", "dateOfBirth", "department", "manager"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Department.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Department.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Company.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Company.class, attributes = {"companyName"}, action = EntityAttributePolicyAction.VIEW)
    void employee();
}