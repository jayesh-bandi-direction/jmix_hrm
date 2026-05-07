package com.company.jmix_hrm.exception;

public class EmployeesExistInDepartmentException extends RuntimeException{

    public EmployeesExistInDepartmentException(String message){
        super(message);
    }

}
