package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Company;

import java.util.List;

public interface CompanyRepository {

    void save(Company company);

    Company findById(long id);

    void update(Company company);

    void delete(long id);

    List<Company> findAll();

    List<Company> findWithPagination(int page, int size);
}
