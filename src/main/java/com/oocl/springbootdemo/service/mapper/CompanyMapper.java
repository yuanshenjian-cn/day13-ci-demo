package com.oocl.springbootdemo.service.mapper;

import com.oocl.springbootdemo.repository.entity.Company;
import com.oocl.springbootdemo.service.dto.CompanyResponse;
import com.oocl.springbootdemo.service.dto.CreateCompanyRequest;
import com.oocl.springbootdemo.service.dto.EmployeeResponse;
import com.oocl.springbootdemo.service.dto.UpdateCompanyRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyMapper {

    public static Company toEntity(CreateCompanyRequest request) {
        Company company = new Company();
        company.setName(request.name());
        return company;
    }

    public static Company toEntity(UpdateCompanyRequest request) {
        Company company = new Company();
        company.setName(request.name());
        return company;
    }

    public static CompanyResponse toResponse(Company company) {
        List<EmployeeResponse> employees = company.getEmployees().stream()
                .map(EmployeeMapper::toResponse)
                .collect(Collectors.toList());

        return new CompanyResponse(company.getId(), company.getName(), employees);
    }

    public static List<CompanyResponse> toCompanyResponseList(List<Company> companies) {
        return companies.stream()
                .map(CompanyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
