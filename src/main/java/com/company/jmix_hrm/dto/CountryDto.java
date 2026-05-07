package com.company.jmix_hrm.dto;

import lombok.Getter;
import lombok.Setter;

// Created this dto to store the response when request is made to third party api to fetch countries
@Setter
@Getter
public class CountryDto {
    private CountryName name;
}
