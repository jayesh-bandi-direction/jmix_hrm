package com.company.jmix_hrm.service;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.exception.DepartmentExistException;
import com.company.jmix_hrm.exception.DepartmentNotFoundException;
import com.company.jmix_hrm.exception.EmployeesExistInDepartmentException;
import io.jmix.audit.EntityLog;
import io.jmix.core.DataManager;
import io.jmix.flowui.model.DataContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DepartmentService {

    private final DataManager dataManager;

    private static final String BASE = "_base";
    private final EntityLog entityLog;

    public DepartmentService(DataManager dataManager, EntityLog entityLog) {
        this.dataManager = dataManager;
        this.entityLog = entityLog;
    }

    //    Add department method
    @Transactional
    public void addDepartment(Department newDepartment, Company company, DataContext dataContext) {
//            Checking if the department which is being added, does it already exist in the db or not
        for (Department department : company.getDepartments()) {
            if (department.getDepartmentName().equalsIgnoreCase(newDepartment.getDepartmentName()))
                throw new DepartmentExistException("Department With Name " + newDepartment.getDepartmentName() + " Exist In " + company.getCompanyName());
            if (department.getDepartmentCode().equalsIgnoreCase(newDepartment.getDepartmentCode()))
                throw new DepartmentExistException("Department With Code " + newDepartment.getDepartmentCode() + " Exist In " + company.getCompanyName());
        }

//      This will save the record in the db but the data context still thinks it is not saved that's why a pop-up window is displayed when we try to
//      navigate to other view before updating the data context
//      dataManager.save(newDepartment);

//      Data context tracks all the changes done in the entity field in the ui and then will call the data manager to save in the db
        dataContext.save();
        entityLog.registerCreate(newDepartment);
    }

    //    edit department method
    public void editDepartment(Department existingDepartment, Company company, DataContext dataContext) {
        for (Department department : company.getDepartments()) {
            if (department.getDepartmentName().equalsIgnoreCase(existingDepartment.getDepartmentName()) && department.getDepartmentCode().equalsIgnoreCase(existingDepartment.getDepartmentCode()))
                throw new DepartmentExistException("Department With Name " + existingDepartment.getDepartmentName() + " Or Code: " + department.getDepartmentCode() + " Exist In " + company.getCompanyName());
        }

        dataContext.save();
    }


    //    remove department method
    @Transactional
    public void removeDepartment(Department department) {

//        checking if employees exists in department
        if (!department.getEmployees().isEmpty())
            throw new EmployeesExistInDepartmentException(department.getEmployees().size() + " Employees Exist In " + department.getDepartmentName());

//            remove if no employees exist in department
        dataManager.remove(department);
        entityLog.registerDelete(department);
    }

    //    get department with employees method
    public Department getDepartmentWithEmployees(UUID departmentId) {
        return dataManager.load(Department.class) // Specifying which entity to fetch
                .id(departmentId) // Specifying the department id
                .fetchPlan(department -> { // method used to specify fetch plan
                    department.addFetchPlan(BASE); // all fields of the department should be loaded
                    department.add("employees", employee -> { // department have list of employee, so we have to specify fetch plan for individual employee
                        employee.addFetchPlan(BASE); // all fields of the employee should be loaded
                        employee.add("user", BASE); // all fields of the user should be loaded
                    });
                })
                .optional()
                .orElseThrow(() -> new DepartmentNotFoundException("Department Not Found With ID: " + departmentId));
    }

    public Optional<Department> getDepartmentWithCode(String departmentCode) {
        return dataManager.unconstrained()
                .load(Department.class)
                .query("select d from Department d where d.departmentCode = :departmentCode")
                .parameter("departmentCode", departmentCode)
                .optional();
    }

    public boolean isDepartmentUniqueInCompany(String departmentName, String companyCode) {
        List<Department> departments = dataManager.load(Department.class)
                .query("select d from Department d where d.company.companyCode= :companyCode")
                .parameter("companyCode", companyCode)
                .list();
        return departments.stream().filter(department -> department.getDepartmentName().equalsIgnoreCase(departmentName)).toList().isEmpty();
    }

    public boolean isDepartmentCodeUnique(Department department) {
        Optional<Department> optionalDepartment = dataManager.load(Department.class)
                .query("select d from Department d where d.departmentCode = :departmentCode")
                .parameter("departmentCode", department.getDepartmentCode())
                .optional();
        if (optionalDepartment.isEmpty())
            return true;
        else {
            return optionalDepartment.get().getDepartmentId().equals(department.getDepartmentId());
        }
    }

    public boolean isDepartmentNameUniqueInCompany(Department department) {
        Optional<Department> optionalDepartment = dataManager.load(Department.class)
                .query("select d from Department d where d.departmentName = :departmentName and d.company.companyCode= :companyCode")
                .parameter("departmentName", department.getDepartmentName())
                .parameter("companyCode", department.getCompany().getCompanyCode())
                .optional();
        if (optionalDepartment.isEmpty())
            return true;
        else {
            return optionalDepartment.get().getDepartmentId().equals(department.getDepartmentId());
        }
    }

}
