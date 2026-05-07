package com.company.jmix_hrm.view.user;

import com.company.jmix_hrm.entity.User;
import com.company.jmix_hrm.service.EmployeeService;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import io.jmix.core.Messages;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.fragment.Fragment;
import io.jmix.flowui.fragment.FragmentDescriptor;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import org.springframework.security.core.userdetails.UserDetails;

@FragmentDescriptor("user-fragment.xml")
public class UserFragment extends Fragment<HorizontalLayout> {

    @ViewComponent
    NativeLabel employeeCodeValueLabel;

    @ViewComponent
    NativeLabel employeeEmailValueLabel;

    @ViewComponent
    NativeLabel employeeDesignationValueLabel;

    @ViewComponent
    NativeLabel employeeCodeLabel;

    @ViewComponent
    NativeLabel employeeEmailLabel;

    @ViewComponent
    NativeLabel employeeDesignationLabel;

    private final Messages messages;

    private final CurrentAuthentication currentAuthentication;

    private final EmployeeService employeeService;

    public UserFragment(CurrentAuthentication currentAuthentication, EmployeeService employeeService, Messages messages){
        this.currentAuthentication = currentAuthentication;
        this.employeeService = employeeService;
        this.messages = messages;
    }

    @Subscribe
    public void onReadyUserFragment(ReadyEvent event) {
//        Will get the current logged-in user
        UserDetails userDetails = currentAuthentication.getUser();

//        Type casting from UserDetails to User
        User user = (User) userDetails;

//        Calling service method
        User userExist = employeeService.getUser(user.getId());

//        Assigning text to label field using the getMessage() method which fetches the value from the messages___.properties
        employeeCodeLabel.setText(messages.getMessage("com.company.jmix_hrm.view.user/codeLabel"));
        employeeEmailLabel.setText(messages.getMessage("com.company.jmix_hrm.view.user/emailLabel"));
        employeeDesignationLabel.setText(messages.getMessage("com.company.jmix_hrm.view.user/designationLabel"));


//        Assigning text to value field
        employeeEmailValueLabel.setText(userExist.getEmail());
        employeeCodeValueLabel.setText(userExist.getEmployee() == null ? "admin" : userExist.getEmployee().getEmployeeCode());
        employeeDesignationValueLabel.setText(userExist.getEmployee() == null ? "null" : userExist.getEmployee().getDesignation().getId());
    }

}