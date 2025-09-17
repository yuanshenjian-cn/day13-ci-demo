package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Employee;
import com.oocl.springbootdemo.repository.jpa.EmployeeJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepositoryDBImpl implements EmployeeRepository {
    private final EmployeeJpaRepository jpaRepository;

    public EmployeeRepositoryDBImpl(EmployeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Employee employee) {
        jpaRepository.save(employee);
    }

    @Override
    public Employee findById(long id) {
        return jpaRepository.findById(id).get();
    }

    @Override
    public void update(Employee employee) {
        jpaRepository.save(employee);
    }

    @Override
    public List<Employee> queryByGender(String gender) {
        return jpaRepository.findByGenderIgnoreCase(gender);
    }

    @Override
    public void delete(long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Employee> findWithPagination(int page, int size) {
        return jpaRepository.findAll(Pageable.ofSize(size).withPage(page - 1)).getContent();
    }
}
