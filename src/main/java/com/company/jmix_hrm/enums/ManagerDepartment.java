package com.company.jmix_hrm.enums;

import io.jmix.core.metamodel.datatype.EnumClass;
import lombok.Getter;

@Getter
public enum ManagerDepartment implements EnumClass<String> {

    SET_MANAGER("Set Manager"),
    SET_DEPARTMENT("Set Department");

    private final String id;

    ManagerDepartment(String id){
        this.id = id;
    }

}
