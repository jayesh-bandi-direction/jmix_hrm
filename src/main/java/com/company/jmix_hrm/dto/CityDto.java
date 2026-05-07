package com.company.jmix_hrm.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// Created this dto to store the JSON response when a request is made to third party api
@Setter
@Getter
public class CityDto {
    private boolean error;
    private String msg;
    private List<String> data;
}
