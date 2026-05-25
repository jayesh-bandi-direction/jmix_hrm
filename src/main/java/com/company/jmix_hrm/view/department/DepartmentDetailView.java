package com.company.jmix_hrm.view.department;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.exception.CompanyNotFoundException;
import com.company.jmix_hrm.exception.DepartmentExistException;
import com.company.jmix_hrm.service.CompanyService;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.security.core.userdetails.UserDetails;


@Route(value = "departments/:id", layout = MainView.class)
@ViewController(id = "Department.detail")
@ViewDescriptor(path = "department-detail-view.xml")
@EditedEntityContainer("departmentDc")
@DialogMode(width = "90%", height = "AUTO")
public class DepartmentDetailView extends StandardDetailView<Department> {

    @ViewComponent
    private Button addDepartmentButton;

    @ViewComponent
    private Button editDepartmentButton;

    @ViewComponent
    private ComboBox<Company> companyComboBox;

    private final transient Notifications notifications;

    private final transient ViewNavigators viewNavigators;

    private final transient CompanyService companyService;

    private final transient DepartmentService departmentService;

    private final transient Dialogs dialogs;

    private final transient CurrentAuthentication currentAuthentication;

    private final transient EmployeeService employeeService;

    //    Constructor Injection
    public DepartmentDetailView(Notifications notifications, ViewNavigators viewNavigators, CompanyService companyService, DepartmentService departmentService, Dialogs dialogs, CurrentAuthentication currentAuthentication, EmployeeService employeeService) {
        this.notifications = notifications;
        this.viewNavigators = viewNavigators;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.dialogs = dialogs;
        this.currentAuthentication = currentAuthentication;
        this.employeeService = employeeService;
    }

    //    This event will get triggered before the UI shown,
//    If the department object received from the list view is not null then will show the edit button
//    Else the department object fields are null then show the add button
    @Subscribe
    public void onBeforeShowDepartmentDetailView(BeforeShowEvent event) {
//        If the user clicked on add button then the instance of the department will have fields with null value
//        If values are present then show the edit button and if null then show the save button
        Department department = getEditedEntity();
        if (department.getDepartmentName() != null) {
            addDepartmentButton.setVisible(false);
            editDepartmentButton.setVisible(true);
        } else {
            addDepartmentButton.setVisible(true);
            editDepartmentButton.setVisible(false);
        }

//        Fetching the current user details
        UserDetails userDetails = currentAuthentication.getUser();
        User user = (User) userDetails;

//        Checking if the user is admin
        if (!user.getUsername().equals("admin")) {
//            if not then fetch all the values of the user
            User userEmployeeDepartmentCompany = employeeService.getUserDataInDetail(user.getId());
//            checking the role of user, if manager then set the company
            if (userEmployeeDepartmentCompany.getEmployee().getDesignation().getId().equals("Manager")) {
                companyComboBox.setValue(userEmployeeDepartmentCompany.getEmployee().getDepartment().getCompany());
//                making the combobox as read only so that the manager cannot select other company
                companyComboBox.setReadOnly(true);
            }
        }

    }

    //    action to be performed when clicked on save button
    @Subscribe("addDepartmentButton")
    public void addDepartment(ClickEvent<Button> clickEvent) {

//        getEditedEntity() - gets the department object currently shown in the form
        Department department = getEditedEntity();

//        Condition to check if the company is selected or not
        if (department.getCompany() == null) {
            notifications.create("Please Select Company").withPosition(Notification.Position.TOP_CENTER).show();
        } else {
            try {
//                Calling service method to get Company and if not found then will throw an exception
                Company company = companyService.getCompanyById(department.getCompany().getCompanyId());

//                to get the currently opened view data context
                DataContext dataContext = getViewData().getDataContext();

//                Calling service method to add department and if the department exist with the same name or code then will throw an exception
                departmentService.addDepartment(department, company, dataContext);

//                message to show on success
//                notifications.create(department.getDepartmentName() + " added successfully in " + company.getCompanyName())
//                        .withPosition(Notification.Position.TOP_CENTER)
//                        .show();

//                message to show on success
                dialogs.createMessageDialog().withHeader("Success").withText(department.getDepartmentName() + " added in " + company.getCompanyName()).open();

//                This method will close the detail view opened as dialog window
                closeWithDefaultAction();

//                navigate to list view after success
                viewNavigators.view(this, DepartmentListView.class).navigate();

            } catch (CompanyNotFoundException | DepartmentExistException exception) {
                notifications.create(exception.getMessage()).withPosition(Notification.Position.TOP_CENTER).show();
            }
        }
    }

    //    action to be performed when clicked on edit button
    @Subscribe("editDepartmentButton")
    private void editDepartment(ClickEvent<Button> clickEvent) {

        Department department = getEditedEntity();

        try {
//            Service method to get company by id and if not found then exception is thrown
            Company company = companyService.getCompanyById(department.getCompany().getCompanyId());

//            Service method to edit the department and if existing with same name or code then throw exception
            departmentService.editDepartment(department, company, getViewData().getDataContext());

//            notifications.create(department.getDepartmentName() + " updated successfully in " + company.getCompanyName())
//                    .withPosition(Notification.Position.TOP_CENTER)
//                    .show();

            dialogs.createMessageDialog().withHeader("Success").withText(department.getDepartmentName() + " updated in " + company.getCompanyName()).open();

            closeWithDefaultAction();

//            With closeWithDefaultAction() - this will only close the opened dialog window but to see the updated record in the data grid we must navigate again so that
//            the data grid gets loaded with the updated value
            viewNavigators.view(this, DepartmentListView.class)
                    .navigate();

        } catch (CompanyNotFoundException | DepartmentExistException exception) {
            notifications.create(exception.getMessage()).withPosition(Notification.Position.TOP_CENTER).withDuration(1000 * 6).show();
        }
    }

}
