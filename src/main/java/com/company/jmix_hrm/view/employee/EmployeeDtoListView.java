package com.company.jmix_hrm.view.employee;

import com.company.jmix_hrm.entity.Department;
import com.company.jmix_hrm.entity.Employee;
import com.company.jmix_hrm.dto.EmployeeDto;
import com.company.jmix_hrm.exception.EmployeeNotFoundException;
import com.company.jmix_hrm.service.DepartmentService;
import com.company.jmix_hrm.service.EmployeeService;
import com.company.jmix_hrm.view.department.DepartmentListView;
import com.company.jmix_hrm.view.main.MainView;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import io.jmix.gridexportflowui.action.ExcelExportAction;
import io.jmix.gridexportflowui.action.JsonExportAction;
import lombok.Setter;

import java.io.*;
import java.time.LocalDate;
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
    private transient CollectionContainer<EmployeeDto> employeesDtoDc;

    //    binding
    @ViewComponent
    DataGrid<EmployeeDto> employeesDtoDataGrid;

    @ViewComponent
    NativeLabel departmentLabel;

    @ViewComponent
    NativeLabel companyLabel;

    @ViewComponent("employeesDtoDataGrid.excelExportAction")
    private transient ExcelExportAction excelExportAction;

    @ViewComponent("employeesDtoDataGrid.jsonExportAction")
    private transient JsonExportAction jsonExportAction;

    //    used to navigate
    private final transient ViewNavigators viewNavigators;

    //    used to show notifications on ui
    private final transient Notifications notifications;

    //    data manager is responsible for save, update and to fetch the records from the db
    private final transient DataManager dataManager;

    private final transient DepartmentService departmentService;

    private final transient EmployeeService employeeService;

    private final transient Downloader downloader;

    private final transient Dialogs dialogs;

    public EmployeeDtoListView(ViewNavigators viewNavigators, Notifications notifications, DataManager dataManager, DepartmentService departmentService, EmployeeService employeeService, Downloader downloader, Dialogs dialogs) {
        this.viewNavigators = viewNavigators;
        this.notifications = notifications;
        this.dataManager = dataManager;
        this.departmentService = departmentService;
        this.employeeService = employeeService;
        this.downloader = downloader;
        this.dialogs = dialogs;
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
            employeeDto.setGender(employee.getGender() != null ? employee.getGender().getId() : null);
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
        Department department = departmentService.getDepartmentEmployees(departmentId);

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

                Department department = departmentService.getDepartmentEmployees(departmentId);
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
    public void onReadyEmployeeDtoListView(ReadyEvent event) {
        excelExportAction.setFileName(departmentLabel.getText() + "_Employees");
        jsonExportAction.setFileName(departmentLabel.getText() + "_Employees");
    }

    @Subscribe(id = "pdfDownloadButton", subject = "clickListener")
    public void onPdfDownloadButtonClick(final ClickEvent<JmixButton> event) throws IOException {

        try {

//            ByteArrayOutputStream is used to write on memory (RAM)
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont headingFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            PdfFont descriptionFont = PdfFontFactory.createFont(StandardFonts.COURIER);

            Paragraph dateText = new Paragraph("Date: " + LocalDate.now()).setFont(headingFont).setFontSize(15);
            Paragraph departmentText = new Paragraph("Department: " + departmentLabel.getText()).setFont(headingFont).setFontSize(15);
            Paragraph companyText = new Paragraph("Company: " + companyLabel.getText()).setFont(headingFont).setFontSize(15);

            float[] columnWidths = {100F, 100F, 100F, 60F, 200F};
            Table table = new Table(columnWidths);

            Cell firstNameCellHeader = new Cell().add(new Paragraph("FirstName").setFont(descriptionFont).setFontSize(12).setBold());
            Cell lastNameCellHeader = new Cell().add(new Paragraph("LastName").setFont(descriptionFont).setFontSize(12).setBold());
            Cell employeeCodeCellHeader = new Cell().add(new Paragraph("Employee Code").setFont(descriptionFont).setFontSize(12).setBold());
            Cell genderCellHeader = new Cell().add(new Paragraph("Gender").setFont(descriptionFont).setFontSize(12).setBold());
            Cell designationCellHeader = new Cell().add(new Paragraph("Designation").setFont(descriptionFont).setFontSize(12).setBold());

            table.addHeaderCell(firstNameCellHeader);
            table.addHeaderCell(lastNameCellHeader);
            table.addHeaderCell(employeeCodeCellHeader);
            table.addHeaderCell(genderCellHeader);
            table.addHeaderCell(designationCellHeader);

            List<EmployeeDto> employeeDtoList = employeesDtoDc.getItems();

            for (EmployeeDto employeeDto : employeeDtoList) {
                Cell firstNameCellValue = new Cell().add(new Paragraph(employeeDto.getFirstname() != null ? employeeDto.getFirstname() : "").setFont(descriptionFont).setFontSize(12));
                Cell lastNameCellValue = new Cell().add(new Paragraph(employeeDto.getLastname() != null ? employeeDto.getLastname() : "").setFont(descriptionFont).setFontSize(12));
                Cell employeeCodeCellValue = new Cell().add(new Paragraph(employeeDto.getEmployeeCode() != null ? employeeDto.getEmployeeCode() : "").setFont(descriptionFont).setFontSize(12));
                Cell genderCellValue = new Cell().add(new Paragraph(employeeDto.getGender() != null ? employeeDto.getGender() : "").setFont(descriptionFont).setFontSize(12));
                Cell designationCellValue = new Cell().add(new Paragraph(employeeDto.getDesignation() != null ? employeeDto.getDesignation() : "").setFontSize(12).setFont(descriptionFont));

                table.addCell(firstNameCellValue);
                table.addCell(lastNameCellValue);
                table.addCell(employeeCodeCellValue);
                table.addCell(genderCellValue);
                table.addCell(designationCellValue);
            }
            document.add(dateText);
            document.add(departmentText);
            document.add(companyText);
            document.add(new Paragraph());
            document.add(new Paragraph());
            document.add(new Paragraph());
            document.add(table);

            document.close();

            downloader.download(() -> new ByteArrayInputStream(outputStream.toByteArray()), departmentLabel.getText() + "_Employees.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
