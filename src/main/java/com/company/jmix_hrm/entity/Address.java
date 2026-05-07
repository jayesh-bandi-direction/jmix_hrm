package com.company.jmix_hrm.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@JmixEntity
@Entity(name = "Address")
@Table(name = "ADDRESS")
@Store(name = "secondary")
@Getter
@Setter
public class Address {

    @Id
    @JmixGeneratedValue
    @InstanceName
    @Column(name = "ADDRESS_ID")
    private UUID addressId;

    @Column(name = "STREET", nullable = false)
    private String street;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "PINCODE", nullable = false)
    private String pincode;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "EMPLOYEE_ID")
    private UUID employee;

}
