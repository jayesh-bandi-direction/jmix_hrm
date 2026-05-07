package com.company.jmix_hrm.view.employee;

import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import java.util.List;

@Route(value = "employees", layout = MainView.class)
@ViewController(id = "Employee.list")
@ViewDescriptor(path = "employee-list-view.xml")
@LookupComponent("employeesDataGrid")
@DialogMode(width = "90%")
public class EmployeeListView extends StandardListView<Employee> {

    @ViewComponent
    CollectionContainer<Employee> employeesDc;

    private final EmployeeService employeeService;

    public EmployeeListView(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @Subscribe
    public void onBeforeShowEmployeeListView(BeforeShowEvent event){
        List<Employee> managers = employeeService.getManagers();
        employeesDc.setItems(managers);
    }

}
