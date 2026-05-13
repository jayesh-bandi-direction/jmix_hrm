package com.company.jmix_hrm.view.employee;

import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "employees", layout = MainView.class)
@ViewController(id = "Employee.list")
@ViewDescriptor(path = "employee-list-view.xml")
@LookupComponent("employeesDataGrid")
@DialogMode(width = "90%")
public class EmployeeListView extends StandardListView<Employee> {

}
