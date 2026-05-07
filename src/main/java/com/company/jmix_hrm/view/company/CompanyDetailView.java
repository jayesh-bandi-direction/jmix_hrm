package com.company.jmix_hrm.view.company;

import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.service.CompanyService;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;
import java.util.ArrayList;
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

    private List<String> cities = new ArrayList<>();

    private List<String> countries = new ArrayList<>();

    @ViewComponent
    ComboBox<String> cityField;

    @ViewComponent
    private JmixComboBox<String> countryField;

    private final CompanyService companyService;

    public CompanyDetailView(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Subscribe
    public void onInitEvent(InitEvent event) {
        countries = companyService.getCountries();
        countryField.setItems(countries);
    }

    @Subscribe
    public void onBeforeShowOfCompanyDetailView(BeforeShowEvent event) {
        cityField.setReadOnly(countryField.getValue() == null);
    }

    @Subscribe("countryField")
    public void onCountryFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixComboBox<String>, String> event) {
        cities = companyService.getCities(countryField.getValue());
        cityField.setReadOnly(false);
        cityField.setItems(cities);
    }


}
