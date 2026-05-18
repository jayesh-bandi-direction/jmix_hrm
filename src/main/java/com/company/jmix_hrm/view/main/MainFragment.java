package com.company.jmix_hrm.view.main;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.service.EmployeeService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.component.grid.TreeDataGrid;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@FragmentDescriptor("main-fragment.xml")
public class MainFragment extends Fragment<HorizontalLayout> {

    @ViewComponent
    NativeLabel employeeNameValue;

    @ViewComponent
    NativeLabel employeeCodeValue;

    @ViewComponent
    NativeLabel employeeDesignationValue;

    @ViewComponent
    NativeLabel employeeUsernameValue;

    @ViewComponent
    NativeLabel employeeEmailValue;

    @ViewComponent
    NativeLabel employeeDobValue;

    @ViewComponent
    NativeLabel employeeDepartmentValue;

    @ViewComponent
    NativeLabel employeeCompanyValue;

    @ViewComponent
    private transient CollectionContainer<Employee> managerEmployeesDc;

    @ViewComponent
    TreeDataGrid<Employee> managerEmployeesTreeDataGrid;

    @ViewComponent
    Div profileDiv;

    @ViewComponent
    Tabs tabsContainer;

    private final transient EmployeeService employeeService;

    private final transient CurrentAuthentication currentAuthentication;

    private final transient DepartmentService departmentService;

    private static final String UNASSIGNED = "Unassigned";

    private static final String NOT_SPECIFIED = "Not Specified";

    public MainFragment(EmployeeService employeeService, CurrentAuthentication currentAuthentication, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.currentAuthentication = currentAuthentication;
        this.departmentService = departmentService;
    }

    @Subscribe
    public void onReadyEventMainFragment(ReadyEvent event) {

//        currentAuthentication.getUser() - will fetch the user details of the current logged-in user
        UserDetails userDetails = currentAuthentication.getUser();

//        Type casting to user
        User user = (User) userDetails;

//        Calling the employee service method to fetch all the details of the logged-in user (employee, department and company)
        User userEmployee = employeeService.getEmployeeDepartmentCompanyUser(user.getId());

        employeeNameValue.setText(userEmployee.getFirstName() == null ? NOT_SPECIFIED : userEmployee.getFirstName() + " " + userEmployee.getLastName());
        employeeCodeValue.setText(userEmployee.getEmployee() == null ? UNASSIGNED : userEmployee.getEmployee().getEmployeeCode());
        employeeDesignationValue.setText(userEmployee.getEmployee() == null ? UNASSIGNED : userEmployee.getEmployee().getDesignation().getId());
        employeeUsernameValue.setText(userEmployee.getUsername());
        employeeEmailValue.setText(userEmployee.getEmail() == null ? NOT_SPECIFIED : userEmployee.getEmail());
        employeeDobValue.setText(userEmployee.getEmployee().getDateOfBirth() == null ? NOT_SPECIFIED : userEmployee.getEmployee().getDateOfBirth().toString());
        employeeDepartmentValue.setText(userEmployee.getEmployee().getDepartment() == null ? UNASSIGNED : userEmployee.getEmployee().getDepartment().getDepartmentName());
        employeeCompanyValue.setText(userEmployee.getCompany() == null ? UNASSIGNED : userEmployee.getCompany().getCompanyName());

//        Checking if there is employee object present for user and if the employee object has a role of manager
        if (userEmployee.getEmployee() != null && userEmployee.getEmployee().getManager() == null && userEmployee.getEmployee().getDepartment() != null) {
//            Calling department service method which fetches the department object along with the employees and respective user object
            Department department = departmentService.getDepartmentWithEmployees(userEmployee.getEmployee().getDepartment().getDepartmentId());
//            Assigning the employees present in the department in data loader
            managerEmployeesDc.setItems(department.getEmployees());
            profileDiv.setVisible(true);
            managerEmployeesTreeDataGrid.setVisible(false);
        }
//        If the employee is not manager then don't show the component
        else {
            managerEmployeesTreeDataGrid.setVisible(false);
            profileDiv.setVisible(true);
            tabsContainer.setVisible(false);
        }
    }

    @Subscribe("tabsContainer")
    public void onTabsContainerSelectedChange(final Tabs.SelectedChangeEvent event) {
        Optional<String> optionalTabId = event.getSelectedTab().getId();
        if (optionalTabId.isPresent() && optionalTabId.get().equals("treeDataGridTab")) {
            managerEmployeesTreeDataGrid.setVisible(true);
            profileDiv.setVisible(false);
        } else {
            managerEmployeesTreeDataGrid.setVisible(false);
            profileDiv.setVisible(true);
        }
    }

}
