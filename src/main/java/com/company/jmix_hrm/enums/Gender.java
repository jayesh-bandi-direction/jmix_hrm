package com.company.jmix_hrm.enums;

import io.jmix.core.metamodel.datatype.EnumClass;
import lombok.Getter;

@Getter
public enum Gender implements EnumClass<String> {

    MALE("Male"),
    FEMALE("Female");

    private final String id;

    Gender(String id){
        this.id = id;
    }

}
