package com.oocl.springbootdemo.service.mapper;

import com.oocl.springbootdemo.repository.entity.Employee;
import com.oocl.springbootdemo.service.dto.CreateEmployeeRequest;
import com.oocl.springbootdemo.service.dto.EmployeeResponse;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {

    public static Employee toEntity(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.name());
        employee.setAge(request.age());
        employee.setSalary(request.salary());
        employee.setGender(request.gender());
        employee.setCompanyId(request.companyId());
        employee.setActive(true);
        return employee;
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getAge(),
                employee.getSalary(),
                employee.getGender(),
                employee.isActive(),
                employee.getCompanyId()
        );
    }

    public static List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream()
                .map(EmployeeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
