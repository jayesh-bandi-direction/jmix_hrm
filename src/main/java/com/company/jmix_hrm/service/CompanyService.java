package com.company.jmix_hrm.service;

import com.company.jmix_hrm.dto.CityDto;
import com.company.jmix_hrm.dto.CountryDto;
import com.company.jmix_hrm.entity.Company;
import com.company.jmix_hrm.exception.CompanyNotFoundException;
import io.jmix.core.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class CompanyService {

    //    In spring boot application we used to use repository but in jmix we use data manager
    private final DataManager dataManager;

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    //    constructor injection
    public CompanyService(DataManager dataManager, RestTemplate restTemplate) {
        this.dataManager = dataManager;
        this.restTemplate = restTemplate;
    }

    //    To get Company by ID from database
    public Company getCompanyBy(UUID companyId) {
        return dataManager.load(Company.class)
                .id(companyId)
                .optional()
                .orElseThrow(() -> new CompanyNotFoundException("Company Not Found By ID: " + companyId));
    }

    public List<String> getCities(String country) {


        try {
            ResponseEntity<CityDto> response = restTemplate.exchange(
                    "https://countriesnow.space/api/v0.1/countries/cities/q?country=" + country,
                    HttpMethod.GET,
                    null,
                    CityDto.class
            );

            CityDto data = response.getBody();

            if (data == null) {
                return List.of();
            }

            return data.getData();

        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            logger.error("Status Code: {}", exception.getStatusCode());
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<String> getCountries() {

        try {
            ResponseEntity<CountryDto[]> response =
                    restTemplate.exchange(
                            "https://restcountries.com/v3.1/all?fields=name",
                            HttpMethod.GET,
                            null,
                            CountryDto[].class
                    );

            CountryDto[] body = response.getBody();

            if (body == null) {
                return List.of();
            }

            return Arrays.stream(body)
                    .map(c -> c.getName().getCommon())
                    .sorted()
                    .toList();
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            logger.info("Status Code: {}\nException: {}", exception.getStatusCode(), exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }

    }


}
