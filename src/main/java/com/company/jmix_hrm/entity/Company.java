package com.company.jmix_hrm.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

// @JmixEntity - This annotation is used to mark the class as jmix entity so that the jmix can handle it
// @Entity - This is jpa annotation used to mapped with the table in db
// @Table - To define the table name
@JmixEntity
@Entity
@Table(name = "COMPANY")
@Getter
@Setter
public class Company {

//    @JmixGeneratedValue - This is used to generate universal unique identifier
    @Id
    @JmixGeneratedValue
    @Column(name = "COMPANY_ID")
    private UUID companyId;

//    @NotNull - Bean Validation, The validation will be done at the view level before reaching the service layer
//    @InstanceName - Used to display the instance of an entity as text on UI
    @NotNull(message = "COMPANY NAME IS REQUIRED")
    @InstanceName
    @Column(name = "COMPANY_NAME", nullable = false)
    private String companyName;

    @NotNull(message = "COMPANY CODE IS REQUIRED")
    @Column(name = "COMPANY_CODE", nullable = false, unique = true)
    private String companyCode;

    @NotNull(message = "CITY IS REQUIRED")
    @Column(name = "CITY", nullable = false)
    private String city;

    @NotNull(message = "COUNTRY IS REQUIRED")
    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @OneToMany(mappedBy = "company", cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Department> departments = new ArrayList<>();

//    @CreatedAt, @CreatedBy, @UpdatedAt, @UpdatedBy - These annotations will automatically add the created and updated time and also the username of user who created and updated
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<User> users = new ArrayList<>();

}
