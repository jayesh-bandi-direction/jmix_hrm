package com.company.jmix_hrm.view.address;

import com.company.jmix_hrm.entity.Address;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "addresses", layout = MainView.class)
@ViewController(id = "Address.list")
@ViewDescriptor(path = "address-list-view.xml")
@LookupComponent("addressesDataGrid")
@DialogMode(width = "64em")
public class AddressListView extends StandardListView<Address> {
}