package com.company.jmix_hrm.view.department;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.exception.EmployeesExistInDepartmentException;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.view.employee.EmployeeDtoListView;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.editor.DataGridEditor;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;

import java.time.format.DateTimeFormatter;

@Route(value = "departments", layout = MainView.class)
@ViewController(id = "Department.list")
@ViewDescriptor(path = "department-list-view.xml")
@LookupComponent("departmentsDataGrid")
@DialogMode(width = "90%")
public class DepartmentListView extends StandardListView<Department> {


    @ViewComponent
    private transient CollectionLoader<Department> departmentsDl;

    @ViewComponent
    private transient CollectionContainer<Department> departmentsDc;

    @ViewComponent
    DataGrid<Department> departmentsDataGrid;

    private final transient ViewNavigators viewNavigators;

    private final transient Notifications notifications;

    private final transient DepartmentService departmentService;

    private final transient Dialogs dialogs;

    private final transient DialogWindows dialogWindows;

    private final transient DataManager dataManager;

    public DepartmentListView(ViewNavigators viewNavigators, Notifications notifications, DepartmentService departmentService, Dialogs dialogs, DialogWindows dialogWindows, DataManager dataManager) {
        this.viewNavigators = viewNavigators;
        this.notifications = notifications;
        this.departmentService = departmentService;
        this.dialogs = dialogs;
        this.dialogWindows = dialogWindows;
        this.dataManager = dataManager;
    }


    //   Method to get the selected department
    public Department getSelectedDepartmentFromDataGrid() {
//        Get selected department from the data grid
        return departmentsDataGrid.getSingleSelectedItem();
    }

    //    Method to show notification if department is not selected
    public void showDepartmentNotSelectedNotification() {
        notifications.create("Please Select Department!")
                .withPosition(Notification.Position.TOP_CENTER)
                .show();
    }

    @Subscribe
    public void onInitEventDepartment(InitEvent event) {
        DataGridEditor<Department> departmentDataGridEditor = departmentsDataGrid.getEditor();
        departmentsDataGrid.addItemDoubleClickListener(e -> {
            departmentDataGridEditor.editItem(e.getItem());
            Component editorComponent = e.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable) editorComponent).focus();
            }
        });
    }

    //    Specifying the action to be performed when clicked on view employees button
    @Subscribe("viewEmployees")
    public void getEmployees(ClickEvent<Button> clickEvent) {
//        calling the method to check if department selected, if selected then return or else show notification
        Department department = getSelectedDepartmentFromDataGrid();

        if (department == null) {
            showDepartmentNotSelectedNotification();
        } else {
//      Will navigate to the specified view with parameters passed in the url
            viewNavigators.view(this, EmployeeDtoListView.class)
                    .withQueryParameters(QueryParameters.of("departmentId", department.getDepartmentId().toString()))
                    .navigate();
        }
    }

    //    Defining the action to be performed when clicked on remove button
    @Subscribe("removeDepartmentCustomButton")
    public void removeDepartment(ClickEvent<Button> clickEvent) {
//        getting the selected department from the data grid
        Department department = getSelectedDepartmentFromDataGrid();

//        if the department is not selected then show the notification
        if (department == null) {
            showDepartmentNotSelectedNotification();
        } else {
            try {
//                calling service method if employees exist in department then exception will be thrown
                departmentService.removeDepartment(department);

//                loading the departments again to show the updated records in the data grid
                departmentsDl.load();

//                notifications.create(department.getDepartmentName() + " Removed Successfully From " + department.getCompany().getCompanyName())
//                        .withPosition(Notification.Position.TOP_CENTER)
//                        .show();

                dialogs.createMessageDialog()
                        .withHeader("Success")
                        .withText(department.getDepartmentName() + " Removed From " + department.getCompany().getCompanyName())
                        .open();

            } catch (EmployeesExistInDepartmentException exception) {
                notifications.create(exception.getMessage())
                        .withPosition(Notification.Position.TOP_CENTER)
                        .withThemeVariant(NotificationVariant.LUMO_ERROR)
                        .show();
            }
        }
    }

    //    navigating to the department detail view to add the department
    @Subscribe("addDepartmentCustomButton")
    public void addDepartment(ClickEvent<Button> clickEvent) {
//        When we want to navigate to detail view, we have to use the detailView() method and in that we have to specify the entity class
//        viewNavigators.detailView(this, Department.class)
//                .newEntity() // specifying to jmix that we are creating new record
//                .navigate();

//        instead of view navigators, if we dialogWindows to navigate then it will open the view as dialog window
        dialogWindows.detail(this, Department.class)
                .newEntity()
                .open();
    }

    //    navigating to the department detail view to edit the department
    @Subscribe("editDepartmentCustomButton")
    public void editDepartment(ClickEvent<Button> clickEvent) {
//        getting the selected department from the data grid
        Department department = getSelectedDepartmentFromDataGrid();

//        if not selected and then clicked on edit button, then show the notification
        if (department == null) {
            showDepartmentNotSelectedNotification();
        } else {
//            viewNavigators.detailView(this, Department.class)
//                    .editEntity(department)
//                    .navigate();

            dialogWindows.detail(this, Department.class)
                    .editEntity(department)
                    .open();
        }
    }

    @Supply(to = "departmentsDataGrid.createdAt", subject = "renderer")
    private Renderer<Department> departmentsDataGridCreatedAtRenderer() {
//        return new ComponentRenderer<>(department -> {
//            Span span = uiComponents.create(Span.class);
//            span.setText(department.toString());
//            return span;
//        });

//        LitRenderer is used to write HTML directly without using the java component class, it is lightweight and fast as compared to component renderer
        return LitRenderer.<Department>of("<b>${item.createdAt}</b>")
                .withProperty("createdAt", department -> department.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyy' 'HH:mm:ss")));
    }


    //    To save the updated record on database when editor is closed
    @Install(to = "departmentsDataGrid.@editor", subject = "closeListener")
    public void onEditorCloseDepartment(EditorCloseEvent<Department> event) {
        Department department = event.getItem();
        if (departmentService.isDepartmentUniqueInCompany(department.getDepartmentName(), department.getCompany().getCompanyCode())) {
            dataManager.save(department);
            dialogs.createMessageDialog().withHeader("Success").withText("Department Updated!").open();
        } else {
            departmentsDc.setItems(dataManager.load(Department.class).all().list());
            dialogs.createMessageDialog().withHeader("Unique Constraint").withText("Department Exist In Company").open();
        }
        departmentsDc.setItems(dataManager.load(Department.class).all().list());
    }

}
