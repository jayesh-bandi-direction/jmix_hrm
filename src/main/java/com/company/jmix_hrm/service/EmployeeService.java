package com.company.jmix_hrm.service;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.enums.Designation;
import com.company.jmix_hrm.exception.EmployeeNotFoundException;
import com.company.jmix_hrm.exception.UserNotFoundException;
import io.jmix.core.DataManager;
import io.jmix.flowui.model.DataContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final DataManager dataManager;

    private final JavaMailSender javaMailSender;

    public EmployeeService(DataManager dataManager, JavaMailSender javaMailSender) {
        this.dataManager = dataManager;
        this.javaMailSender = javaMailSender;
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

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jb.bandi.direction@gmail.com");
        simpleMailMessage.setTo("jb.bandi.direction@gmail.com");
        simpleMailMessage.setSubject("Unassigned From " + department);
        simpleMailMessage.setText(employee.getUser().getFirstName() + " " + employee.getUser().getLastName() + " Is Unassigned From " + department);

        javaMailSender.send(simpleMailMessage);
    }

    //    get user method
    public User getUser(UUID userId) {
        return dataManager.load(User.class)
                .id(userId)
                .fetchPlan(userFp -> {
                    userFp.addFetchPlan("_base");
                    userFp.add("employee", "_base");
                })
                .optional()
                .orElseThrow(() -> new EmployeeNotFoundException("User Not Found With ID: " + userId));
    }

    //    method to get user, employee, department, company
    public User getEmployeeDepartmentCompanyUser(UUID userId) {
        return dataManager.load(User.class)
                .id(userId)
                .fetchPlan(userFp -> {
                    userFp.addFetchPlan("_base");
                    userFp.add("employee", employeeFp -> {
                        employeeFp.addFetchPlan("_base");
                        employeeFp.add("department", departmentFp -> {
                            departmentFp.addFetchPlan("_base");
                            departmentFp.add("company", companyFp -> companyFp.addFetchPlan("_base"));
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

    public List<Employee> getManagers() {
        List<Employee> employees = dataManager.load(Employee.class)
                .all().list();
        return employees.stream().filter(employee -> employee.getDesignation().equals(Designation.MANAGER)).collect(Collectors.toList());
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

            System.out.println("Company Of User: " + company.getCompanyName());
        } else {
            user.getEmployee().setDepartment(department);

//            We must first track the company, so that it will not be in detached state
            Company company = dataContext.merge(department.getCompany());
            user.setCompany(company);

            System.out.println("Company Of Department: " + department.getCompany().getCompanyName());
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
        Optional<User> optionalExistingUser = dataManager.load(User.class)
                .query("select u from User u where u.employee.employeeCode = :code")
                .parameter("code", user.getEmployee().getEmployeeCode())
                .optional();

        if (optionalExistingUser.isPresent()) {
            return optionalExistingUser.get().getId().equals(user.getId());
        }
        return true;
    }

    public void editEmployee(User user, DataContext dataContext, Employee manager, Department department) {
        if (manager != null) {
            user.getEmployee().setManager(manager);
            user.getEmployee().setDepartment(manager.getDepartment());
        }
        else {
            user.getEmployee().setDepartment(department);
        }
        dataContext.save();
    }
}
