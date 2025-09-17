package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Company;
import com.oocl.springbootdemo.repository.jpa.CompanyJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyRepositoryDbImpl implements CompanyRepository {
    private final CompanyJpaRepository companyJpaRepository;

    public CompanyRepositoryDbImpl(CompanyJpaRepository companyJpaRepository) {
        this.companyJpaRepository = companyJpaRepository;
    }

    @Override
    public void save(Company company) {
        companyJpaRepository.save(company);
    }

    @Override
    public Company findById(long id) {
        return companyJpaRepository.findById(id).orElse(null);
    }

    @Override
    public void update(Company company) {
        companyJpaRepository.save(company);
    }

    @Override
    public void delete(long id) {
        companyJpaRepository.deleteById(id);
    }

    @Override
    public List<Company> findAll() {
        return companyJpaRepository.findAll();
    }

    @Override
    public List<Company> findWithPagination(int page, int size) {
        return companyJpaRepository.findAll(Pageable.ofSize(size).withPage(page - 1)).getContent();
    }
}
