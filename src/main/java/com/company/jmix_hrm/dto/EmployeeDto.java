package com.company.jmix_hrm.dto;

import io.jmix.core.metamodel.annotation.JmixEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Created this dto to store the employee values and to create a view of this dto
@JmixEntity
@Getter
@Setter
public class EmployeeDto{

    private UUID employeeId;

    private String employeeCode;

    private String firstname;

    private String lastname;

    private LocalDate dateOfBirth;

    private String gender;

    private String designation;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

    private Integer version;

}
