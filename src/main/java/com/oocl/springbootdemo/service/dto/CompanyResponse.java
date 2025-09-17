package com.oocl.springbootdemo.service.dto;

import java.util.List;

public record CompanyResponse(long id, String name, List<EmployeeResponse> employees) {
}
