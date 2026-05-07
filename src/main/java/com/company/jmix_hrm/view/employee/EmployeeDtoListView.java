package com.company.jmix_hrm.view.employee;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.dto.EmployeeDto;
import com.company.jmix_hrm.exception.EmployeeNotFoundException;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.department.DepartmentListView;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.gridexportflowui.action.ExcelExportAction;
import io.jmix.gridexportflowui.action.JsonExportAction;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Route(value = "employee-dto", layout = MainView.class)
@ViewDescriptor(path = "employee-dto-list-view.xml")
@ViewController(id = "EmployeeDto.list")
@Setter
public class EmployeeDtoListView extends StandardListView<EmployeeDto> {

    //    created this field to store the departmentId received from the query parameter in the url when navigated to this view
    private UUID departmentId;

    //    binding the collection container from descriptor in controller to access it
    @ViewComponent
    CollectionContainer<EmployeeDto> employeesDtoDc;

    //    binding
    @ViewComponent
    DataGrid<EmployeeDto> employeesDtoDataGrid;

    //    to bind the data loader from descriptor in controller
    @ViewComponent
    CollectionLoader<EmployeeDto> employeesDtoDl;

    @ViewComponent
    NativeLabel departmentLabel;

    @ViewComponent
    NativeLabel companyLabel;

    @ViewComponent("employeesDtoDataGrid.excelExportAction")
    ExcelExportAction excelExportAction;

    @ViewComponent("employeesDtoDataGrid.jsonExportAction")
    JsonExportAction jsonExportAction;

    //    used to navigate
    private final ViewNavigators viewNavigators;

    //    used to show notifications on ui
    private final Notifications notifications;

    //    data manager is responsible for save, update and to fetch the records from the db
    private final DataManager dataManager;

    private final DepartmentService departmentService;

    private final EmployeeService employeeService;

    public EmployeeDtoListView(ViewNavigators viewNavigators, Notifications notifications, DataManager dataManager, DepartmentService departmentService, EmployeeService employeeService) {
        this.viewNavigators = viewNavigators;
        this.notifications = notifications;
        this.dataManager = dataManager;
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    //    this event will get trigger and the method will be executed
    @Subscribe
    public void onQueryParametersChange(QueryParametersChangeEvent event) {
        List<String> departmentIdLabel = event.getQueryParameters() // will get list of query parameters
                .getParameters() // will get them in Map<String, List<String>> type
                .get("departmentId"); // will get the list of string values passed in the specified key query parameter

//        if not null and is not empty then call the set method to save the departmentId
        if (departmentIdLabel != null && !departmentIdLabel.isEmpty()) {
            setDepartmentId(UUID.fromString(departmentIdLabel.getFirst()));
        }
    }

    //    Method to set collection of employees dto in collection container
    public void setEmployeesDtoInDataContainer(Department department) {
        List<EmployeeDto> employeeDtoList = new ArrayList<>();

        for (Employee employee : department.getEmployees()) {
//            Recommended to create instance of entity using the create() in data manager so that it is managed and tracked by the jmix
            EmployeeDto employeeDto = dataManager.create(EmployeeDto.class);
            employeeDto.setEmployeeId(employee.getId());
            employeeDto.setFirstname(employee.getUser().getFirstName());
            employeeDto.setLastname(employee.getUser().getLastName());
            employeeDto.setEmployeeCode(employee.getEmployeeCode());
            employeeDto.setGender(employee.getGender().getId());
            employeeDto.setDesignation(employee.getDesignation().getId());
            employeeDto.setDateOfBirth(employee.getDateOfBirth());
            employeeDto.setCreatedAt(employee.getCreatedAt());
            employeeDto.setCreatedBy(employee.getCreatedBy());
            employeeDto.setUpdatedAt(employee.getUpdatedAt());
            employeeDto.setUpdatedBy(employee.getUpdatedBy());
            employeeDto.setVersion(employee.getVersion());
            employeeDtoList.add(employeeDto);
        }
//        will store the collections in the data container
        employeesDtoDc.setItems(employeeDtoList);
    }

    //    method will get executed before the ui is shown
    @Subscribe
    public void onBeforeShowEmployee(BeforeShowEvent event) {
//        calling the department service and assigning the department
        Department department = departmentService.getDepartmentWithEmployees(departmentId);

//        calling the set method to set the values in the collection container
        setEmployeesDtoInDataContainer(department);

        departmentLabel.setText(department.getDepartmentName());
        companyLabel.setText(department.getCompany().getCompanyName());
    }

    //    method will get executed and will navigate to list view of department when clicked on button
    @Subscribe("backButtonToDepartment")
    public void backToDepartment(ClickEvent<Button> event) {
        viewNavigators.view(this, DepartmentListView.class).navigate();
    }

    //    Action to perform when click on unassign button
    @Subscribe("unassignEmployeeButton")
    public void unassignEmployeeFromDepartment(ClickEvent<Button> event) {
//        getting the selected employee form the data grid
        EmployeeDto employeeDto = employeesDtoDataGrid.getSingleSelectedItem();

//        if not selected then show the notification
        if (employeeDto == null) {
            notifications.create("Please Select Employee")
                    .withPosition(Notification.Position.TOP_CENTER)
                    .show();
        } else {
            try {
                Employee employee = employeeService.getEmployee(employeeDto.getEmployeeId());

                employeeService.unassignEmployee(employee);

                Department department = departmentService.getDepartmentWithEmployees(departmentId);
                setEmployeesDtoInDataContainer(department);

//            Notification to be displayed on successfully unassignment
                notifications
                        .create(employeeDto.getFirstname() + " " + employeeDto.getLastname() + " With Employee Code : " + employeeDto.getEmployeeCode() + " Unassigned Successfully From: " + department.getDepartmentName())
                        .withPosition(Notification.Position.TOP_CENTER)
                        .show();
            } catch (EmployeeNotFoundException exception) {
                notifications.create(exception.getMessage())
                        .withPosition(Notification.Position.TOP_CENTER)
                        .show();
            }
        }
    }

    //    ReadyEvent is triggered after the UI is shown
    @Subscribe
    public void onReady(ReadyEvent event) {
        excelExportAction.setFileName(departmentLabel.getText() + "_Employees");
        jsonExportAction.setFileName(departmentLabel.getText() + "_Employees");
    }

}
