package com.oocl.springbootdemo.service.dto;

public record UpdateEmployeeRequest(String name, Integer age, double salary) {
}