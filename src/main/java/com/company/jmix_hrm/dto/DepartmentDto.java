package com.company.jmix_hrm.dto;

import io.jmix.core.metamodel.annotation.JmixEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO - Data Transfer Object, We have to use the @JmixEntity for dto as well so that the jmix framework can handle it
@JmixEntity
@Getter
@Setter
public class DepartmentDto {

    private UUID departmentId;

    private String departmentName;

    private String departmentCode;

    private Integer employees;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

    private Integer version;

}
