package com.company.jmix_hrm.service;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.enums.Designation;
import com.company.jmix_hrm.exception.EmployeeNotFoundException;
import com.company.jmix_hrm.exception.UserNotFoundException;
import io.jmix.core.DataManager;
import io.jmix.email.EmailException;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.flowui.model.DataContext;
import jakarta.mail.internet.ContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    private final DataManager dataManager;

    private final Emailer emailer;

    private static final String BASE = "_base";

    public EmployeeService(DataManager dataManager, @Lazy Emailer emailer) {
        this.dataManager = dataManager;
        this.emailer = emailer;
    }

    //    get employee method
    public Employee getEmployee(UUID employeeId) {
        return dataManager.load(Employee.class)
                .id(employeeId)
                .optional()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found With ID " + employeeId));
    }

    //    unassign employee method
    public void unassignEmployee(Employee employee) {
        String department = employee.getDepartment().getDepartmentName();
        employee.setDepartment(null);
        employee.setManager(null);

//            updating the employee record in the db
        dataManager.save(employee);
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom("jb.bandi.direction@gmail.com");
//        simpleMailMessage.setTo(employee.getUser().getEmail());
//        simpleMailMessage.setSubject("Unassigned From " + department);
//        simpleMailMessage.setText(employee.getUser().getFirstName() + " " + employee.getUser().getLastName() + " Is Unassigned From " + department);
//        javaMailSender.send(simpleMailMessage);

        String firstName = employee.getUser().getFirstName();
        String lastName = employee.getUser().getLastName();

        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setFrom("jb.bandi.direction@gmail.com")
                .setAddresses(employee.getUser().getEmail())
                .setSubject("[Notification] Department Unassignment Update")
                .setBodyContentType("text/plain; charset=UTF-8")
                .setBody("Dear " + firstName + " " + lastName + ",\n\n" +
                        "This is to notify you that you have been unassigned from the " + department + " department.\n\n" +
                        "Best regards,\n" +
                        "System Administrator")
                .setImportant(false)
                .build();

//        try {
//            emailer.sendEmail(emailInfo);
//        } catch (EmailException e) {
//            throw new RuntimeException(e);
//        }
        emailer.sendEmailAsync(emailInfo);

    }

    //    get user method
    public User getUser(UUID userId) {
        return dataManager.load(User.class)
                .id(userId)
                .fetchPlan(userFp -> {
                    userFp.addFetchPlan(BASE);
                    userFp.add("employee", BASE);
                })
                .optional()
                .orElseThrow(() -> new EmployeeNotFoundException("User Not Found With ID: " + userId));
    }

    //    method to get user, employee, department, company
    public User getEmployeeDepartmentCompanyUser(UUID userId) {
        return dataManager.load(User.class)
                .id(userId)
                .fetchPlan(userFp -> {
                    userFp.addFetchPlan(BASE);
                    userFp.add("employee", employeeFp -> {
                        employeeFp.addFetchPlan(BASE);
                        employeeFp.add("department", departmentFp -> {
                            departmentFp.addFetchPlan(BASE);
                            departmentFp.add("company", companyFp -> companyFp.addFetchPlan(BASE));
                        });
                    });
                })
                .optional()
                .orElseThrow(() -> new UserNotFoundException("User Not Found With ID: " + userId));
    }

    public List<Employee> getEmployeesByManagerId(UUID managerId) {
        return dataManager.load(Employee.class)
                .query("select e from Employee e where e.manager.employeeId = :managerId")
                .parameter("managerId", managerId)
                .list();
    }

    public List<Employee> getManagers(Company company) {
        List<Employee> employees;
        if (company != null) {
            employees = dataManager.load(Employee.class)
                    .query("select e from Employee e where e.department.company.companyId = :companyId")
                    .parameter("companyId", company.getCompanyId())
                    .list();
        } else {
            employees = dataManager.load(Employee.class)
                    .all()
                    .list();
        }
        return employees.stream().filter(employee -> employee.getDesignation().equals(Designation.MANAGER)).toList();
    }

    public boolean checkIfUserEmailIsUnique(String email) {
        Optional<User> optionalUser = dataManager.load(User.class)
                .query("select u from User u where u.email = :email")
                .parameter("email", email)
                .optional();
        return optionalUser.isEmpty();
    }

    public boolean checkIfUsernameIsUnique(String username) {
        Optional<User> optionalUser = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .optional();
        return optionalUser.isEmpty();
    }

    public boolean checkIfEmployeeCodeIsUnique(String employeeCode) {
        Optional<User> optionalUser = dataManager.load(User.class)
                .query("select u from User u where u.employee.employeeCode =:employeeCode")
                .parameter("employeeCode", employeeCode)
                .optional();
        return optionalUser.isEmpty();
    }

    public void addEmployee(User user, DataContext dataContext, Employee manager, Department department) {
        if (manager != null) {

            user.getEmployee().setManager(manager);
            user.getEmployee().setDepartment(manager.getDepartment());

//            We must first track the company, so that it will not be in detached state
            Company company = dataContext.merge(manager.getDepartment().getCompany());
            user.setCompany(company);
        } else {
            user.getEmployee().setDepartment(department);

//            We must first track the company, so that it will not be in detached state
            Company company = dataContext.merge(department.getCompany());
            user.setCompany(company);
        }
        dataContext.save();
    }

    public boolean checkIfEmailBelongsToSameUser(User user) {
        Optional<User> optionalExistingUser = dataManager.load(User.class)
                .query("select u from User u where u.email = :email")
                .parameter("email", user.getEmail())
                .optional();

        if (optionalExistingUser.isPresent()) {
            return optionalExistingUser.get().getId().equals(user.getId());
        }
        return true;
    }

    public boolean checkIfCodeBelongsToSameUser(User user) {

        if (user.getEmployee() != null) {
            Optional<User> optionalExistingUser = dataManager.load(User.class)
                    .query("select u from User u where u.employee.employeeCode = :code")
                    .parameter("code", user.getEmployee().getEmployeeCode())
                    .optional();

            if (optionalExistingUser.isPresent()) {
                return optionalExistingUser.get().getId().equals(user.getId());
            }
        }
        return true;
    }

    public void editEmployee(User user, DataContext dataContext, Employee manager, Department department) {

        User mergedUser = dataContext.merge(user);
        Employee mergedEmployee = dataContext.merge(mergedUser.getEmployee());

        if (manager != null) {
            Employee mergedManager = dataContext.merge(manager);
            Department mergedDepartment = dataContext.merge(manager.getDepartment());
            Company mergedCompany = dataContext.merge(manager.getDepartment().getCompany());
            mergedEmployee.setManager(mergedManager);
            mergedEmployee.setDepartment(mergedDepartment);
            mergedUser.setCompany(mergedCompany);
        } else {
            Department mergedDepartment = dataContext.merge(department);
            Company mergedCompany = dataContext.merge(department.getCompany());
            mergedEmployee.setManager(null);
            mergedEmployee.setDepartment(mergedDepartment);
            mergedUser.setCompany(mergedCompany);
        }

        mergedUser.setEmployee(mergedEmployee);
        dataContext.save();
    }

    public Optional<User> findByUsername(String username) {
        return dataManager
                .unconstrained()
                .load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .optional();
    }

    public long getUsersCount() {
        return dataManager
                .load(User.class)
                .all()
                .list()
                .size();
    }
}
