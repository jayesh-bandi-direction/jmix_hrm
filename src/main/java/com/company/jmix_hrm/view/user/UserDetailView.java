package com.company.jmix_hrm.view.user;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.enums.Designation;
import com.company.jmix_hrm.enums.Gender;
import com.company.jmix_hrm.enums.ManagerDepartment;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.core.Metadata;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.component.radiobuttongroup.JmixRadioButtonGroup;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

@Route(value = "users/:id", layout = MainView.class)
@ViewController(id = "User.detail")
@ViewDescriptor(path = "user-detail-view.xml")
@EditedEntityContainer("userDc")
@DialogMode(width = "90%", height = "AUTO")
public class UserDetailView extends StandardDetailView<User> {

    @ViewComponent
    private TypedTextField<String> usernameField;

    @ViewComponent
    private PasswordField passwordField;

    @ViewComponent
    private PasswordField confirmPasswordField;

//    @ViewComponent
//    private ComboBox<String> timeZoneField;

    @ViewComponent
    private MessageBundle messageBundle;

    @Autowired
    private Notifications notifications;

    @Autowired
    private EntityStates entityStates;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean newEntity;

    @Autowired
    private Metadata metadata;

    @Autowired
    private EmployeeService employeeService;

    @ViewComponent
    private CollectionContainer<Employee> employeesDc;

    @ViewComponent
    private TypedTextField<String> firstNameField;

    @ViewComponent
    private TypedTextField<String> lastNameField;

    @ViewComponent
    private TypedTextField<String> emailField;

    @ViewComponent
    private JmixComboBox<Designation> employeeDesignationField;

    @ViewComponent
    private RadioButtonGroup<Gender> genderField;

    @ViewComponent
    private TypedDatePicker<LocalDate> employeeDateOfBirthField;

    @ViewComponent
    private TypedTextField<String> employeeCodeField;

    @ViewComponent
    private EntityComboBox<Employee> employeeManagerField;

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private Dialogs dialogs;

    @ViewComponent
    private JmixButton saveNewEmployeeButton;

    @ViewComponent
    private JmixButton saveExistingEmployeeButton;

    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private EntityComboBox<Department> departmentField;

    @Subscribe
    public void onInit(final InitEvent event) {
//        timeZoneField.setItems(List.of(TimeZone.getAvailableIDs()));
    }

    //    InitEntityEvent - When New Instance Of An Entity Is Getting Created
    @Subscribe
    public void onInitEntity(final InitEntityEvent<User> event) {
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);

        User user = event.getEntity();

        Employee employee = metadata.create(Employee.class);
        employee.setUser(user);
        user.setEmployee(employee);
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            usernameField.focus();
        }
    }

    @Subscribe
    public void onValidation(final ValidationEvent event) {
        if (entityStates.isNew(getEditedEntity())
                && !Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
            event.getErrors().add(messageBundle.getMessage("passwordsDoNotMatch"));
        }
    }

    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));

            newEntity = true;
        }
    }

    @Subscribe
    public void onAfterSave(final AfterSaveEvent event) {
        if (newEntity) {
            notifications.create(messageBundle.getMessage("noAssignedRolesNotification"))
                    .withThemeVariant(NotificationVariant.LUMO_WARNING)
                    .withPosition(Notification.Position.TOP_END)
                    .show();

            newEntity = false;
        }
    }

    @Subscribe
    public void onBeforeShowUserDetailView(BeforeShowEvent event) {
        employeesDc.setItems(employeeService.getManagers());
        User user = getEditedEntity();
        if (user.getUsername() == null) {
            saveNewEmployeeButton.setVisible(true);
            saveExistingEmployeeButton.setVisible(false);
        } else {
            saveNewEmployeeButton.setVisible(false);
            saveExistingEmployeeButton.setVisible(true);
        }
        employeeManagerField.setVisible(false);
        departmentField.setVisible(false);
    }

    @Subscribe(id = "saveNewEmployeeButton")
    public void onSaveNewEmployeeButtonClick(ClickEvent<Button> event) {

        boolean canAdd = true;

        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            canAdd = false;
            notifications.create("Password Does Not Match").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (usernameField.getValue().isBlank()) {
            canAdd = false;
            notifications.create("Username Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (firstNameField.getValue().isBlank()) {
            canAdd = false;
            notifications.create("FirstName Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (lastNameField.getValue().isBlank()) {
            canAdd = false;
            notifications.create("LastName Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (emailField.getValue().isBlank()) {
            canAdd = false;
            notifications.create("Email Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (employeeDesignationField.getValue() == null) {
            canAdd = false;
            notifications.create("Designation Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (genderField.getValue() == null) {
            canAdd = false;
            notifications.create("Gender Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (employeeDateOfBirthField.getValue() == null) {
            canAdd = false;
            notifications.create("DOB Is Required").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (passwordField.getValue().isBlank()) {
            canAdd = false;
            notifications.create("Password Cannot Be Blank").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
        }

        if (canAdd) {
            User user = getEditedEntity();
            DataContext dataContext = getViewData().getDataContext();

            if (!employeeService.checkIfUserEmailIsUnique(user.getEmail()))
                notifications.create("User Exist With Email: " + user.getEmail()).withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();

            if (!employeeService.checkIfUsernameIsUnique(user.getUsername()))
                notifications.create("User Exist With Username: " + user.getUsername()).withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();

            if (!employeeService.checkIfEmployeeCodeIsUnique(user.getEmployee().getEmployeeCode()))
                notifications.create("User Exist With Employee Code: " + user.getEmployee().getEmployeeCode()).withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();

            if (employeeManagerField.getValue() == null && departmentField.getValue() == null)
                notifications.create("Manager Or Department Is Required To Add Employee").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_WARNING).show();
            else {

//            Employee managerEmployee = employeeManagerField.getValue();
//            System.out.println("Manager: " + managerEmployee);


                employeeService.addEmployee(user, dataContext, employeeManagerField.getValue(), departmentField.getValue());

                dialogs.createMessageDialog()
                        .withHeader("Success")
                        .withText("Employee Record Added Successfully!")
                        .open();

                closeWithDefaultAction();
                viewNavigators.view(this, UserListView.class)
                        .navigate();
            }
        }
    }

    @Subscribe(id = "saveExistingEmployeeButton")
    public void onSaveExistingEmployeeButtonClick(ClickEvent<Button> event) {
        User user = getEditedEntity();
        DataContext dataContext = getViewData().getDataContext();

        boolean uniqueEmail = employeeService.checkIfEmailBelongsToSameUser(user);
        boolean uniqueCode = employeeService.checkIfCodeBelongsToSameUser(user);

        if (!uniqueEmail)
            notifications.create("Employee Exist With Email: " + user.getEmail()).withPosition(Notification.Position.TOP_CENTER).show();

        if (!uniqueCode)
            notifications.create("Employee Exist With Code: " + user.getEmployee().getEmployeeCode()).withPosition(Notification.Position.TOP_CENTER).show();

        if (uniqueEmail && uniqueCode) {

            Employee manager = employeeManagerField.getValue();

            Department department = departmentField.getValue();

            if (manager == null && department == null)
                notifications.create("Please Select Manager Or Department").withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_WARNING).show();
            else {
                employeeService.editEmployee(user, dataContext, employeeManagerField.getValue(), departmentField.getValue());

                dialogs.createMessageDialog()
                        .withHeader("Success")
                        .withText("Employee Record Updated Successfully!")
                        .open();

                closeWithDefaultAction();
                viewNavigators.view(this, UserListView.class)
                        .navigate();

            }
        }
    }

    @Subscribe("radioButtonManagerDepartment")
    public void onRadioButtonManagerDepartmentComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixRadioButtonGroup<ManagerDepartment>, ManagerDepartment> event) {
        if (event.getValue().equals(ManagerDepartment.SET_MANAGER)) {
            employeeManagerField.setVisible(true);
            departmentField.setVisible(false);
        } else {
            employeeManagerField.setVisible(false);
            departmentField.setVisible(true);
        }
    }
}
