package com.company.jmix_hrm.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Entity
@Table(name = "DEPARTMENT")
@Getter
@Setter
public class Department {

    @Id
    @JmixGeneratedValue
    @Column(name = "DEPARTMENT_ID")
    private UUID departmentId;

    @NotNull(message = "DEPARTMENT NAME IS REQUIRED")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "DEPARTMENT NAME CANNOT CONTAIN SPECIAL CASE CHARACTERS")
    @Column(name = "DEPARTMENT_NAME", nullable = false)
    private String departmentName;

    @NotNull(message = "DEPARTMENT CODE IS REQUIRED")
//    @InstanceName
    @Column(name = "DEPARTMENT_CODE", nullable = false, unique = true)
    private String departmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

//    When a department is deleted, the employees in that department will be unlinked and not deleted
    @OneToMany(mappedBy = "department", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    @OnDelete(DeletePolicy.UNLINK)
    private List<Employee> employees = new ArrayList<>();

    @CreatedDate
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    @InstanceName
    public String getInstanceName(){
        return departmentName + " - " + company.getCompanyName();
    }

}
