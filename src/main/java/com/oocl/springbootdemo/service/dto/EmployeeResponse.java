package com.oocl.springbootdemo.service.dto;

public record EmployeeResponse(long id, String name, int age, double salary, String gender, boolean active, long companyId) {
}