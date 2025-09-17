package com.oocl.springbootdemo.service;

import com.oocl.springbootdemo.repository.CompanyRepositoryInmemoryImpl;
import com.oocl.springbootdemo.repository.entity.Company;
import com.oocl.springbootdemo.service.dto.CompanyResponse;
import com.oocl.springbootdemo.service.dto.CreateCompanyRequest;
import com.oocl.springbootdemo.service.dto.CreateCompanyResponse;
import com.oocl.springbootdemo.service.dto.UpdateCompanyRequest;
import com.oocl.springbootdemo.service.exception.CompanyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CompanyServiceTest {
    @Mock
    private CompanyRepositoryInmemoryImpl companyRepositoryInmemoryImpl;

    private CompanyService companyService;
    
    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepositoryInmemoryImpl);
    }

    @Test
    void should_create_company_when_given_valid_company() {
        CreateCompanyRequest request = new CreateCompanyRequest("OOCL");

        doAnswer(invocation -> {
            Company arg = invocation.getArgument(0);
            arg.setId(1);
            return null;
        }).when(companyRepositoryInmemoryImpl).save(any(Company.class));

        CreateCompanyResponse result = companyService.create(request);

        assertEquals(1, result.id());
        verify(companyRepositoryInmemoryImpl, times(1)).save(any(Company.class));
    }

    @Test
    void should_return_single_company_given_exist_company_id() {
        Company company = new Company();
        company.setId(1);
        company.setName("OOCL");
        company.setEmployees(Arrays.asList());

        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(company);

        CompanyResponse foundCompany = companyService.querySingle(1);
        assertEquals(foundCompany.id(), company.getId());
        assertEquals(foundCompany.name(), company.getName());

        verify(companyRepositoryInmemoryImpl, times(1)).findById(1);
    }

    @Test
    void should_throw_exception_given_company_not_existed() {
        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(null);

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.querySingle(1);
        });
        verify(companyRepositoryInmemoryImpl, times(1)).findById(1);
    }

    @Test
    void should_update_company_when_given_valid_company() {
        Company existingCompany = new Company();
        existingCompany.setId(1);
        existingCompany.setName("OOCL");

        UpdateCompanyRequest updateRequest = new UpdateCompanyRequest("OOCL Updated");

        Company updatedCompany = new Company();
        updatedCompany.setId(1);
        updatedCompany.setName("OOCL Updated");
        updatedCompany.setEmployees(Arrays.asList());

        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(existingCompany).thenReturn(updatedCompany);

        CompanyResponse result = companyService.update(1, updateRequest);

        assertEquals(updateRequest.name(), result.name());
        verify(companyRepositoryInmemoryImpl, times(2)).findById(1);
        verify(companyRepositoryInmemoryImpl, times(1)).update(any(Company.class));
    }

    @Test
    void should_throw_exception_when_update_non_existing_company() {
        UpdateCompanyRequest updateRequest = new UpdateCompanyRequest("OOCL Updated");

        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(null);

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.update(1, updateRequest);
        });
        verify(companyRepositoryInmemoryImpl, times(1)).findById(1);
        verify(companyRepositoryInmemoryImpl, never()).update(any());
    }

    @Test
    void should_delete_company_when_given_existing_id() {
        Company company = new Company();
        company.setId(1);
        company.setName("OOCL");

        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(company);

        companyService.delete(1);

        verify(companyRepositoryInmemoryImpl, times(1)).findById(1);
        verify(companyRepositoryInmemoryImpl, times(1)).delete(1);
    }

    @Test
    void should_throw_exception_when_delete_non_existing_company() {
        when(companyRepositoryInmemoryImpl.findById(1)).thenReturn(null);

        assertThrows(CompanyNotFoundException.class, () -> {
            companyService.delete(1);
        });
        verify(companyRepositoryInmemoryImpl, times(1)).findById(1);
        verify(companyRepositoryInmemoryImpl, never()).delete(anyLong());
    }

    @Test
    void should_return_all_companies() {
        Company company1 = new Company();
        company1.setId(1);
        company1.setName("OOCL");
        company1.setEmployees(Arrays.asList());

        Company company2 = new Company();
        company2.setId(2);
        company2.setName("Spring");
        company2.setEmployees(Arrays.asList());

        List<Company> companies = Arrays.asList(company1, company2);

        when(companyRepositoryInmemoryImpl.findAll()).thenReturn(companies);

        List<CompanyResponse> result = companyService.queryAll();

        assertEquals(2, result.size());
        assertEquals("OOCL", result.get(0).name());
        assertEquals("Spring", result.get(1).name());
        verify(companyRepositoryInmemoryImpl, times(1)).findAll();
    }

    @Test
    void should_return_paginated_companies() {
        Company company1 = new Company();
        company1.setId(1);
        company1.setName("Company 1");
        company1.setEmployees(Arrays.asList());

        Company company2 = new Company();
        company2.setId(2);
        company2.setName("Company 2");
        company2.setEmployees(Arrays.asList());

        List<Company> companies = Arrays.asList(company1, company2);

        when(companyRepositoryInmemoryImpl.findWithPagination(1, 2)).thenReturn(companies);

        List<CompanyResponse> result = companyService.queryWithPagination(1, 2);

        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).name());
        assertEquals("Company 2", result.get(1).name());
        verify(companyRepositoryInmemoryImpl, times(1)).findWithPagination(1, 2);
    }
}
