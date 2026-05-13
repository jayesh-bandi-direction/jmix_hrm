package com.company.jmix_hrm.config;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.enums.Designation;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.service.EmployeeService;
import io.jmix.core.DataManager;
import io.jmix.ldap.userdetails.AbstractLdapUserDetailsSynchronizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * <p>Creating a class tha extends the AbstractLdapUserDetailsSynchronizationStrategy to create a user record if not exit in db but present in ldap server</p>
 */
@Component
public class UserSynchronizationWithLdap extends AbstractLdapUserDetailsSynchronizationStrategy<User> {

//    To create logs
    Logger logger = LoggerFactory.getLogger(UserSynchronizationWithLdap.class);

    private final DataManager dataManagerService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public UserSynchronizationWithLdap(DataManager dataManagerService, EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.dataManagerService = dataManagerService;
        this.departmentService = departmentService;
    }

//    Implementing abstract method, This will return the class type for which the record will be created
    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }

//    Implementing abstract method, DirContextOperations - Used to fetch the values of the attribute
    @Override
    protected void mapUserDetailsAttributes(User user, DirContextOperations ctx) {

//        Service method to fetch user by username, 'uid' attribute in user entry contains the username
        Optional<User> optionalUser = employeeService.findByUsername(ctx.getStringAttribute("uid"));
        logger.info("User Record Present: {}", optionalUser.isPresent());

//        If no username present then create the user record
        if (optionalUser.isEmpty()) {
            logger.info("Creating User And Employee Record");

//            creating employee object
            Employee employee = dataManagerService.create(Employee.class);

//            fetching the department by departmentCode
            String departmentNumber = ctx.getStringAttribute("departmentNumber");
            logger.info("Department Number: {}", departmentNumber);
            Optional<Department> optionalDepartment = departmentService.getDepartmentWithCode(departmentNumber);

            logger.info("Department Present: {}", optionalDepartment.isPresent());

//            if department present then set the department in employee and also set the company of user
            if (optionalDepartment.isPresent()) {
                logger.info("Department Name: {}", optionalDepartment.get().getDepartmentName());
                logger.info("Company Name: {}", optionalDepartment.get().getCompany().getCompanyName());
                employee.setDepartment(optionalDepartment.get());
                user.setCompany(optionalDepartment.get().getCompany());
            }


//            assigning the required fields
            user.setUsername(ctx.getStringAttribute("uid"));
            user.setFirstName(ctx.getStringAttribute("cn"));
            user.setLastName(ctx.getStringAttribute("sn"));
            user.setEmail(ctx.getStringAttribute("mail"));
            employee.setEmployeeCode(ctx.getStringAttribute("displayName"));

//            to check the employee designation
            String employeeType = ctx.getStringAttribute("employeeType");

//            assigning the designation based on the value
            switch (employeeType) {
                case "systemAdmin" -> employee.setDesignation(Designation.SYSTEM_ADMIN);
                case "manager" -> employee.setDesignation(Designation.MANAGER);
                case "traineeSoftwareEngineer" -> employee.setDesignation(Designation.TRAINEE_SOFTWARE_ENGINEER);
                case "softwareEngineer" -> employee.setDesignation(Designation.SOFTWARE_ENGINEER);
                case "seniorSoftwareEngineer" -> employee.setDesignation(Designation.SENIOR_SOFTWARE_ENGINEER);
                case "humanResource" -> employee.setDesignation(Designation.HUMAN_RESOURCE);
                default -> employee.setDesignation(Designation.OTHER_DESIGNATION);
            }

//            assigning the user in the employee
            employee.setUser(user);
//            assigning the employee in the user
            user.setEmployee(employee);
        }

    }

}
