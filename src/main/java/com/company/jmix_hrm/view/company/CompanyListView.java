package com.company.jmix_hrm.view.company;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.view.department.DepartmentDtoListView;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.*;

@Route(value = "companies", layout = MainView.class)
@ViewController("Company.list")
@ViewDescriptor("company-list-view.xml")
@LookupComponent("companiesDataGrid")
@DialogMode(width = "70%")
public class CompanyListView extends StandardListView<Company> {

    //    To sent notification
    private final transient Notifications notifications;

    //    To navigate to specified view
    private final transient ViewNavigators viewNavigators;

    private final transient DialogWindows dialogWindows;

    //    Binding The DataGrid Component Present In Company List View (Descriptor)
    @ViewComponent
    DataGrid<Company> companiesDataGrid;

    //    Constructor Injection
    public CompanyListView(Notifications notifications, ViewNavigators viewNavigators, DialogWindows dialogWindows) {
        this.notifications = notifications;
        this.viewNavigators = viewNavigators;
        this.dialogWindows = dialogWindows;
    }

    //    Defining the action to be performed when clicked on button with id "viewDepartmentsButton"
    @Subscribe("viewDepartmentsButton")
    public void getDepartments(ClickEvent<Button> clickEvent) {

//        Will Get The Record That Is Selected In The DataGrid
        Company company = companiesDataGrid.getSingleSelectedItem();

//        If Record Not Selected Then Show Notification
        if (company == null) {
            notifications.create("Please Select The Company!").withPosition(Notification.Position.TOP_CENTER).show();
        } else {
//            will navigate to the specified view, and we can specify what to pass in the url
            viewNavigators.view(this, DepartmentDtoListView.class)
                    .withQueryParameters(QueryParameters.of("companyId", company.getCompanyId().toString()))
                    .navigate();

        }
    }

    @Subscribe("customAddCompanyButton")
    public void addCompany(ClickEvent<Button> event) {
        dialogWindows.detail(this, Company.class)
                .newEntity()
                .open();
//        viewNavigators.detailView(this, Company.class)
//                .newEntity()
//                .navigate();
    }

    @Subscribe("customEditCompanyButton")
    public void editCompany(ClickEvent<Button> event) {

        Company company = companiesDataGrid.getSingleSelectedItem();

        if (company == null)
            notifications.create("Please Select Company").withPosition(Notification.Position.TOP_CENTER).show();
        else {
            dialogWindows.detail(this, Company.class)
                    .editEntity(company)
                    .open();

//            viewNavigators.detailView(this, Company.class)
//                    .editEntity(company)
//                    .navigate();
        }
    }
}