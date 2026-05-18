package com.company.jmix_hrm.view.address;

import com.company.jmix_hrm.entity.Address;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "addresses", layout = MainView.class)
@ViewController(id = "Address.list")
@ViewDescriptor(path = "address-list-view.xml")
@LookupComponent("addressesDataGrid")
@DialogMode(width = "64em")
public class AddressListView extends StandardListView<Address> {

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    CollectionContainer<Address> addressesDc;

    @Install(to = "addressesDataGrid.@editor", subject = "closeListener")
    public void onEditorCloseEventAddress(EditorCloseEvent<Address> event) {
        Address address = event.getItem();
        Address savedAddress = dataManager.save(address);
        addressesDc.replaceItem(savedAddress);
    }
}