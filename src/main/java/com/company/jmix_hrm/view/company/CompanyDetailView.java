package com.company.jmix_hrm.view.company;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.service.CompanyService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;
import java.util.List;

/*
@Route - Used to specify the url path and also in which view it should be displayed
@ViewController - Used to specify the controller id and this id can be used in user menu to display it
@ViewDescriptor - Used to specify the descriptor path to bind with controller
@EditedEntityContainer - Used to specify the container id so that with this view we will be able to do changes in the instance of entity
@DialogMode - Used to display the view as dialog (pop up screen)
 */
@Route(value = "companies/:id", layout = MainView.class)
@ViewController(id = "Company.detail")
@ViewDescriptor(path = "company-detail-view.xml")
@EditedEntityContainer("companyDc")
@DialogMode(width = "90%", height = "AUTO")
public class CompanyDetailView extends StandardDetailView<Company> {

    @ViewComponent
    ComboBox<String> cityField;

    @ViewComponent
    private JmixComboBox<String> countryField;

    private final transient CompanyService companyService;

    public CompanyDetailView(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Subscribe
    public void onInitEvent(InitEvent event) {
//        Setting the items in the combobox component before the view is shown
        countryField.setItems(companyService.getCountries());
    }

    @Subscribe
    public void onBeforeShowOfCompanyDetailView(BeforeShowEvent event) {
//        Based on the value selected in the country combobox component setting the readOnly of city combobox
        cityField.setReadOnly(countryField.getValue() == null);
    }

    @Subscribe("countryField")
    public void onCountryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<String>, String> event) {
//        This method will be executed when the user select any one item from the combobox which changes the value
        List<String> cities = companyService.getCities(countryField.getValue());
        cityField.setReadOnly(false);
        cityField.setItems(cities);
    }

}
