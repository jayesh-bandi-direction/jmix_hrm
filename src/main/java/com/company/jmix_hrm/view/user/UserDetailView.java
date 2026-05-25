package com.company.jmix_hrm.view.user;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.enums.Designation;
import com.company.jmix_hrm.enums.Gender;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
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
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.*;

@Route(value = "users/:id", layout = MainView.class)
@ViewController(id = "User.detail")
@ViewDescriptor(path = "user-detail-view.xml")
@EditedEntityContainer("userDc")
@DialogMode(width = "90%", height = "AUTO")
public class UserDetailView extends StandardDetailView<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailView.class);

    private boolean newEntity;

    @ViewComponent
    private TypedTextField<String> usernameField;

    @ViewComponent
    private PasswordField passwordField;

    @ViewComponent
    private PasswordField confirmPasswordField;

//    @ViewComponent
//    private ComboBox<String> timeZoneField;

    @ViewComponent
    private transient MessageBundle messageBundle;

    @ViewComponent
    private transient CollectionContainer<Employee> employeesDc;

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
    private EntityComboBox<Employee> employeeManagerField;

    @ViewComponent
    private JmixButton saveNewEmployeeButton;

    @ViewComponent
    private JmixButton saveExistingEmployeeButton;

    @ViewComponent
    private EntityComboBox<Department> departmentField;

    @ViewComponent
    private transient CollectionContainer<Department> departmentsDc;

    private final transient CurrentAuthentication currentAuthentication;

    private final transient Notifications notifications;

    private final transient EntityStates entityStates;

    private final transient PasswordEncoder passwordEncoder;

    private final transient Metadata metadata;

    private final transient EmployeeService employeeService;

    private final transient ViewNavigators viewNavigators;

    private final transient Dialogs dialogs;

    public UserDetailView(CurrentAuthentication currentAuthentication, Notifications notifications, EntityStates entityStates, PasswordEncoder passwordEncoder, Metadata metadata, EmployeeService employeeService, ViewNavigators viewNavigators, Dialogs dialogs) {
        this.currentAuthentication = currentAuthentication;
        this.notifications = notifications;
        this.entityStates = entityStates;
        this.passwordEncoder = passwordEncoder;
        this.metadata = metadata;
        this.employeeService = employeeService;
        this.viewNavigators = viewNavigators;
        this.dialogs = dialogs;
    }

    private void showErrorNotification(String notificationMessage){
        notifications.create(notificationMessage).withPosition(Notification.Position.TOP_CENTER).withThemeVariant(NotificationVariant.LUMO_ERROR).show();
    }

    private boolean validateFields(){

        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            showErrorNotification("Password Does Not Match");
            return false;
        }

        if (usernameField.getValue().isBlank()) {
            showErrorNotification("Username Is Required");
            return false;
        }

        if (firstNameField.getValue().isBlank()) {
            showErrorNotification("FirstName Is Required");
            return false;
        }

        if (lastNameField.getValue().isBlank()) {
            showErrorNotification("LastName Is Required");
            return false;
        }

        if (emailField.getValue().isBlank()) {
            showErrorNotification("Email Is Required");
            return false;
        }

        if (employeeDesignationField.getValue() == null) {
            showErrorNotification("Designation Is Required");
            return false;
        }

        if (genderField.getValue() == null) {
            showErrorNotification("Gender Is Required");
            return false;
        }

        if (employeeDateOfBirthField.getValue() == null) {
            showErrorNotification("DOB Is Required");
            return false;
        }

        if (passwordField.getValue().isBlank()) {
            showErrorNotification("Password Cannot Be Blank");
            return false;
        }
        return true;
    }

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
    public void onReadyUserDetailView(final ReadyEvent event) {
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

//        Current User Details
        User currentUser = (User) currentAuthentication.getUser();
        User currentUserDetails = employeeService.getUserDataInDetail(currentUser.getId());

//        Fetching All The Managers Of The Current Employee Company
        List<Employee> managers = employeeService.getManagers(currentUserDetails.getCompany());

//        If The Current User Is System Admin Then Show All The Managers And If The User Is Manager Then Can Only Set Themselves In Data Container
        if (currentUserDetails.getEmployee().getDesignation().equals(Designation.MANAGER)) {
//            Storing The Current Manager
            List<Employee> currentManager = managers.stream().filter(manager -> manager.getId().equals(currentUserDetails.getEmployee().getId())).toList();
            employeesDc.setItems(currentManager);

            List<Department> departments = new ArrayList<>();
            departments.add(currentManager.getFirst().getDepartment());
            departmentsDc.setItems(departments);

        } else if (currentUserDetails.getEmployee().getDesignation().equals(Designation.SYSTEM_ADMIN)) {
            employeesDc.setItems(managers);
        } else if (currentUserDetails.getEmployee().getDesignation().equals(Designation.TRAINEE_SOFTWARE_ENGINEER)) {
            employeeManagerField.setReadOnly(true);
            departmentField.setReadOnly(true);
        } else {
            logger.info("Other User");
        }

        User user = getEditedEntity();
        if (user.getUsername() == null) {
            saveNewEmployeeButton.setVisible(true);
            saveExistingEmployeeButton.setVisible(false);

        } else {
            saveNewEmployeeButton.setVisible(false);
            saveExistingEmployeeButton.setVisible(true);
        }
    }

    @Subscribe(id = "saveNewEmployeeButton")
    public void onSaveNewEmployeeButtonClick(ClickEvent<Button> event) {

        if (validateFields()) {
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
                employeeService.editEmployee(user, dataContext, manager, department);

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

    @Subscribe("employeeManagerField")
    public void onEmployeeManagerFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Employee>, Employee> event) {
        if (event.getValue() != null) {
            Employee manager = event.getValue();
            departmentField.setValue(manager.getDepartment());
        }
    }

}
