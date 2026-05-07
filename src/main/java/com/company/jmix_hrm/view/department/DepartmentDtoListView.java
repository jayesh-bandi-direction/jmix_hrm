package com.company.jmix_hrm.view.department;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.dto.DepartmentDto;
import com.company.jmix_hrm.exception.CompanyNotFoundException;
import com.company.jmix_hrm.service.CompanyService;
import com.company.jmix_hrm.view.company.CompanyListView;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import io.jmix.gridexportflowui.action.ExcelExportAction;
import io.jmix.gridexportflowui.action.JsonExportAction;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Route(value = "departments-dto", layout = MainView.class)
@ViewController(id = "DepartmentDto.list")
@ViewDescriptor(path = "department-dto-list-view.xml")
@Setter
public class DepartmentDtoListView extends StandardListView<DepartmentDto> {

    //    created this field so that the values that are received from the url query parameters can be stored
    private UUID companyId;

    //    Binding the container present in the descriptor so that it can be used in the controller
    @ViewComponent
    CollectionContainer<DepartmentDto> departmentsDtoDc;

    @ViewComponent
    NativeLabel companyLabel;

    //    Binding the action present in descriptor to use in controller
    @ViewComponent("departmentsDtoDataGrid.excelExportAction")
    ExcelExportAction excelExportAction;

    @ViewComponent("departmentsDtoDataGrid.jsonExportAction")
    JsonExportAction jsonExportAction;

    //    Data Manager in jmix is used to save, get and update the records in db
    private final DataManager dataManager;

    //    Used to navigate to the specified view
    private final ViewNavigators viewNavigators;

    //    Service
    private final CompanyService companyService;

    //    Notification
    private final Notifications notifications;

    //    Constructor Injection
    public DepartmentDtoListView(DataManager dataManager, ViewNavigators viewNavigators, CompanyService companyService, Notifications notifications) {
        this.dataManager = dataManager;
        this.viewNavigators = viewNavigators;
        this.companyService = companyService;
        this.notifications = notifications;
    }

    //    This event will be triggered when we navigate to this view when there is parameters present in the url
    @Subscribe
    public void onQueryParametersChangeEvent(QueryParametersChangeEvent event) {
//        getQueryParameters() - Will get all the parameters in the url
//        getParameters() - Will return the parameters in Map<String, List<String>> type
//        get("companyId") - Will get the value of the specified key parameter in Map
        List<String> companyIdLabel = event.getQueryParameters().getParameters().get("companyId");
        if (companyIdLabel != null && !companyIdLabel.isEmpty()) {
//            calling the set method to set the uuid in companyId
            setCompanyId(UUID.fromString(companyIdLabel.getFirst()));
        }
    }

    //    This event will be triggered before the ui is shown
    @Subscribe
    public void onBeforeShowEvent(BeforeShowEvent event) {

        try {
//            Calling the service method
            Company company = companyService.getCompanyBy(companyId);

            List<DepartmentDto> departmentDtoList = new ArrayList<>();
            for (Department department : company.getDepartments()) {
//            In jmix, it is better to create the instance by using the create() method of the data manager so that it can be managed by jmix
                DepartmentDto departmentDto = dataManager.create(DepartmentDto.class);
                departmentDto.setDepartmentId(department.getDepartmentId());
                departmentDto.setDepartmentName(department.getDepartmentName());
                departmentDto.setDepartmentCode(department.getDepartmentCode());
                departmentDto.setCreatedAt(department.getCreatedAt());
                departmentDto.setCreatedBy(department.getCreatedBy());
                departmentDto.setUpdatedAt(department.getUpdatedAt());
                departmentDto.setUpdatedBy(department.getUpdatedBy());
                departmentDto.setVersion(department.getVersion());
                departmentDto.setEmployees(department.getEmployees().size());
                departmentDtoList.add(departmentDto);
            }

//            Adding the collection in the data container component present in the descriptor so that the data grid is shown with values
            departmentsDtoDc.setItems(departmentDtoList);

//            To set the company name on department dto title
            companyLabel.setText(company.getCompanyName());

        } catch (CompanyNotFoundException exception) {
//            Notification to show if the company is not found by id
            notifications.create(exception.getMessage())
                    .withPosition(Notification.Position.TOP_CENTER)
                    .show();
        }
    }

    //    Will be triggered on click event and will navigate to the specified view
    @Subscribe("backButton")
    public void backMethod(ClickEvent<Button> clickEvent) {
        viewNavigators.view(this, CompanyListView.class).navigate();
    }

    //    Will be triggered after the UI is shown
    @Subscribe
    public void onReadyDepartmentDtoListView(ReadyEvent event) {
        excelExportAction.setFileName(companyLabel.getText() + "_Departments");
        jsonExportAction.setFileName(companyLabel.getText() + "_Departments");
    }

}
