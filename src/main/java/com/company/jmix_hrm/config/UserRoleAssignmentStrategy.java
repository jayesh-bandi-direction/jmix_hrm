package com.company.jmix_hrm.config;

import io.jmix.ldap.userdetails.LdapUserAdditionalRoleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

/**
 * To assign the role to the user when the user login for first time we create a class that implements the LdapUserAdditionalRoleProvider interface
 */
@Component
public class UserRoleAssignmentStrategy implements LdapUserAdditionalRoleProvider {

//    For creating logs
    Logger logger = LoggerFactory.getLogger(UserRoleAssignmentStrategy.class);

    /**
     * <p>LdapUserAdditionalRoleProvider contains abstract method getAdditionalRoles(DirContextOperations user, String username) which contains the logic to assign the role.</p>
     * @param user the user for which it is necessary to calculate additional roles
     * @param username
     * @return
     */
    @Override
    public Set<GrantedAuthority> getAdditionalRoles(DirContextOperations user, String username) {

//        GrantedAuthority is an interface that represents the roles/permissions in spring security
        Set<GrantedAuthority> roles = new HashSet<>();
//        "ui-minimal" is the code of the role
        GrantedAuthority minimalAuthority = new SimpleGrantedAuthority("ui-minimal");
        roles.add(minimalAuthority);

//        employeeType is an attribute in user entry which contains the user designation value and based on that assigning the roles
        String employeeType = user.getStringAttribute("employeeType");

        switch (employeeType) {
            case "manager" -> {
                GrantedAuthority managerResourceAuthority = new SimpleGrantedAuthority("manager-resource-role");
                GrantedAuthority managerRowAuthority = new SimpleGrantedAuthority("manager-row-level-role");
                roles.add(managerResourceAuthority);
                roles.add(managerRowAuthority);
            }
            case "traineeSoftwareEngineer" -> {
                GrantedAuthority traineeResourceAuthority = new SimpleGrantedAuthority("trainee-resource-role");
                GrantedAuthority traineeRowAuthority = new SimpleGrantedAuthority("trainee-row-level-role");
                roles.add(traineeResourceAuthority);
                roles.add(traineeRowAuthority);
            }
            case "systemAdmin" -> {
                GrantedAuthority systemAdminAuthority = new SimpleGrantedAuthority("system-full-access");
                roles.add(systemAdminAuthority);
            }
            default -> logger.info("No employeeType Specified");
        }

        return roles;
    }
}
