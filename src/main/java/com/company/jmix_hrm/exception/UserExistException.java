package com.company.jmix_hrm.exception;

public class UserExistException extends RuntimeException{
    public UserExistException(String message){
        super(message);
    }
}
