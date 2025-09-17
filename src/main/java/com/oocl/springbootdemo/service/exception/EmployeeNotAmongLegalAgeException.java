package com.oocl.springbootdemo.service.exception;

public class EmployeeNotAmongLegalAgeException extends RuntimeException {
    public EmployeeNotAmongLegalAgeException(String message) {
        super(message);
    }
}