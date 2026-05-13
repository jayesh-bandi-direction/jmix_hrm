package com.company.jmix_hrm.view.address;

import com.company.jmix_hrm.entity.Address;
import com.company.jmix_hrm.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "addresses/:id", layout = MainView.class)
@ViewController(id = "Address.detail")
@ViewDescriptor(path = "address-detail-view.xml")
@EditedEntityContainer("addressDc")
public class AddressDetailView extends StandardDetailView<Address> {
}