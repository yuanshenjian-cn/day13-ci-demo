package com.oocl.springbootdemo.service.exception;

public class EmployeeInactiveException extends RuntimeException {
    public EmployeeInactiveException(String message) {
        super(message);
    }
}