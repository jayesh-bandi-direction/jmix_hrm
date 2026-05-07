package com.company.jmix_hrm.view.user;

import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.*;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {

    private final UiComponents uiComponents;

    private final EmployeeService employeeService;

    private final ViewNavigators viewNavigators;

    private final Notifications notifications;

    private final CurrentAuthentication currentAuthentication;

    private final DialogWindows dialogWindows;

    @ViewComponent
    private DataGrid<User> usersDataGrid;

    @ViewComponent
    private JmixButton customAddButton;


    public UserListView(UiComponents uiComponents, EmployeeService employeeService, ViewNavigators viewNavigators, Notifications notifications, CurrentAuthentication currentAuthentication, DialogWindows dialogWindows) {
        this.uiComponents = uiComponents;
        this.employeeService = employeeService;
        this.viewNavigators = viewNavigators;
        this.notifications = notifications;
        this.currentAuthentication = currentAuthentication;
        this.dialogWindows = dialogWindows;
    }

    @Subscribe
    public void beforeShowUserListView(BeforeShowEvent event) {
        UserDetails userDetails = currentAuthentication.getUser();
        User currentUser = (User) userDetails;
        User currentUserDetails = employeeService.getEmployeeDepartmentCompanyUser(currentUser.getId());
        if (currentUserDetails.getEmployee() != null && currentUserDetails.getEmployee().getDesignation().getId().equals("Trainee Software Engineer")) {
            customAddButton.setVisible(false);
        }
    }

    @Supply(to = "usersDataGrid.active", subject = "renderer")
    public Renderer<User> dataGridUserComponentRenderer() {
        return new ComponentRenderer<>(user -> {
            Div div = uiComponents.create(Div.class);
//            div.setClassName("active-field-style");
            div.getStyle().setBorderRadius("10px");
            div.getStyle().setPaddingTop("4px");
            div.getStyle().setPaddingBottom("4px");
            div.getStyle().setWidth("90%");
            div.getStyle().set("font-weight", "bold");
            if (Boolean.TRUE.equals(user.getActive())) {
                div.setText("Active");
                div.getStyle().setBackgroundColor("#9AD872");
            } else {
                div.getStyle().setBackgroundColor("#E74646");
                div.setText("Inactive");
            }
            div.getStyle().setDisplay(Style.Display.FLEX);
            div.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
            return div;
        });
    }

    @Subscribe("customAddButton")
    public void addEmployee(ClickEvent<Button> clickEvent) {

//        viewNavigators.detailView(this, User.class)
//                .newEntity()
//                .navigate();

        dialogWindows.detail(this, User.class)
                .newEntity()
                .open();
    }

    @Subscribe("customEditButton")
    public void editButton(ClickEvent<Button> clickEvent) {

        User user = usersDataGrid.getSingleSelectedItem();

        if (user == null) {
            notifications.create("Please Select Employee!").withPosition(Notification.Position.TOP_CENTER).show();
        } else {
//            viewNavigators.detailView(this, User.class)
//                    .editEntity(user)
//                    .navigate();

            dialogWindows.detail(this, User.class)
                    .editEntity(user)
                    .open();
        }
    }

}