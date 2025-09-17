package com.oocl.springbootdemo.service.exception;

public class EmployeeSalaryLimitationException extends RuntimeException {
    public EmployeeSalaryLimitationException(String message) {
        super(message);
    }
}