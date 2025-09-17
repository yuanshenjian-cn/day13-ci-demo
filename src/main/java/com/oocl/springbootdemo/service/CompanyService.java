package com.oocl.springbootdemo.service;

import com.oocl.springbootdemo.repository.CompanyRepository;
import com.oocl.springbootdemo.repository.entity.Company;
import com.oocl.springbootdemo.service.dto.CompanyResponse;
import com.oocl.springbootdemo.service.dto.CreateCompanyRequest;
import com.oocl.springbootdemo.service.dto.CreateCompanyResponse;
import com.oocl.springbootdemo.service.dto.UpdateCompanyRequest;
import com.oocl.springbootdemo.service.exception.CompanyNotFoundException;
import com.oocl.springbootdemo.service.mapper.CompanyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CreateCompanyResponse create(CreateCompanyRequest request) {
        Company company = CompanyMapper.toEntity(request);
        companyRepository.save(company);
        return new CreateCompanyResponse(company.getId());
    }

    @Transactional
    public CompanyResponse update(long id, UpdateCompanyRequest request) {
        Company existedCompany = companyRepository.findById(id);
        if (existedCompany == null) {
            throw new CompanyNotFoundException("Company not found with id: " + id);
        }
        Company companyToBeUpdated = CompanyMapper.toEntity(request);
        companyToBeUpdated.setId(id);
        companyRepository.update(companyToBeUpdated);
        Company updatedCompany = companyRepository.findById(id);
        return CompanyMapper.toResponse(updatedCompany);
    }

    public CompanyResponse querySingle(long id) {
        Company company = companyRepository.findById(id);
        if (company == null) {
            throw new CompanyNotFoundException("Company not found with id: " + id);
        }
        return CompanyMapper.toResponse(company);
    }

    public List<CompanyResponse> queryAll() {
        List<Company> companies = companyRepository.findAll();
        return CompanyMapper.toCompanyResponseList(companies);
    }

    public List<CompanyResponse> queryWithPagination(int page, int size) {
        List<Company> companies = companyRepository.findWithPagination(page, size);
        return CompanyMapper.toCompanyResponseList(companies);
    }

    @Transactional
    public void delete(long id) {
        Company companyToBeDeleted = companyRepository.findById(id);
        if (companyToBeDeleted == null) {
            throw new CompanyNotFoundException("Company not found with id: " + id);
        }
        companyRepository.delete(id);
    }
}
