package com.company.jmix_hrm.view.user;

import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import org.springframework.security.core.userdetails.UserDetails;


@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {

    private final transient UiComponents uiComponents;

    private final transient EmployeeService employeeService;

    private final transient ViewNavigators viewNavigators;

    private final transient Notifications notifications;

    private final transient CurrentAuthentication currentAuthentication;

    private final transient DialogWindows dialogWindows;

    @ViewComponent
    private DataGrid<User> usersDataGrid;

    @ViewComponent
    private JmixButton customAddButton;
    @ViewComponent
    private JmixButton Excel;

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
        if (currentUserDetails.getEmployee() != null && currentUserDetails.getEmployee().getDesignation() != null && currentUserDetails.getEmployee().getDesignation().getId().equals("Trainee Software Engineer")) {
            customAddButton.setVisible(false);
            Excel.setVisible(false);
        }
    }

    @Supply(to = "usersDataGrid.active", subject = "renderer")
    public Renderer<User> dataGridUserComponentRenderer() {
        return new ComponentRenderer<>(user -> {
            Span parentSpan = uiComponents.create(Span.class);
            Span childSpan = uiComponents.create(Span.class);
            Icon icon = uiComponents.create(Icon.class);

            parentSpan.getElement().getThemeList().add("badge");
            parentSpan.setClassName("parent-span");
            childSpan.getStyle().set("text-transform", "uppercase");
            childSpan.getStyle().setFontWeight(Style.FontWeight.BOLD);
            icon.setClassName("icon-size");

            if (Boolean.TRUE.equals(user.getActive())) {
                parentSpan.getElement().getThemeList().add("success");
                childSpan.setText("Active");
                icon.setIcon(VaadinIcon.CHECK_CIRCLE);
            } else {
                parentSpan.getElement().getThemeList().add("error");
                childSpan.setText("Inactive");
                icon.setIcon(VaadinIcon.CLOSE_CIRCLE);
            }
            parentSpan.add(icon);
            parentSpan.add(childSpan);
            return parentSpan;
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
//
//    @Install(to = "pagination", subject = "totalCountByRepositoryDelegate")
//    private Long paginationTotalCountByRepositoryDelegate(final JmixDataRepositoryContext jmixDataRepositoryContext) {
//        return employeeService.getUsersCount();
//    }

}