package com.oocl.springbootdemo.service.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateEmployeeRequest(
        String name, 
        int age, 
        @Min(value = 0, message = "Salary must be positive number")
        double salary, 
        @NotNull(message = "Gender cannot be null")
        String gender, 
        long companyId) {
}