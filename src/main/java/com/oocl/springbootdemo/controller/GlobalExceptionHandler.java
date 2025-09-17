package com.oocl.springbootdemo.controller;

import com.oocl.springbootdemo.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(
            {
                    EmployeeNotFoundException.class,
                    CompanyNotFoundException.class
            }
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleEmployeeNotFoundException(Exception e) {
        e.printStackTrace();
    }

    @ExceptionHandler(
            {
                    EmployeeSalaryLimitationException.class,
                    EmployeeInactiveException.class,
                    EmployeeNotAmongLegalAgeException.class
            }
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmployeeSalaryLimitationException(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
