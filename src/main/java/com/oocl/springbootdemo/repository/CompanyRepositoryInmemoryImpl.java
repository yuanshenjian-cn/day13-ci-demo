package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Company;

import java.util.ArrayList;
import java.util.List;

//@Repository
public class CompanyRepositoryInmemoryImpl implements CompanyRepository {
    private final List<Company> companies = new ArrayList<>();

    public void save(Company company) {
        company.setId(companies.size() + 1);
        companies.add(company);
    }

    public Company findById(long id) {
        return companies.stream().filter(company -> company.getId() == id).findFirst()
                .orElse(null);
    }

    public void update(Company company) {
        Company existed = findById(company.getId());
        if (existed != null) {
            existed.setName(company.getName());
        }
    }

    public void delete(long id) {
        Company company = findById(id);
        if (company != null) {
            companies.remove(company);
        }
    }

    public List<Company> findAll() {
        return new ArrayList<>(companies);
    }

    public List<Company> findWithPagination(int page, int size) {
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, companies.size());

        if (startIndex >= companies.size()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(companies.subList(startIndex, endIndex));
    }
}
