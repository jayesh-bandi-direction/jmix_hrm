package com.company.jmix_hrm.enums;

import io.jmix.core.metamodel.datatype.EnumClass;
import lombok.Getter;

@Getter
public enum Designation implements EnumClass<String> {

    TRAINEE_SOFTWARE_ENGINEER("Trainee Software Engineer"),
    SOFTWARE_ENGINEER("Software Engineer"),
    SENIOR_SOFTWARE_ENGINEER("Senior Software Engineer"),
    MANAGER("Manager"),
    HUMAN_RESOURCE("Human Resource");

    private final String id;

    Designation(String id) {
        this.id = id;
    }

}
