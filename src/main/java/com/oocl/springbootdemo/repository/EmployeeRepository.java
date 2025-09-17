package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Employee;

import java.util.List;

public interface EmployeeRepository {
    void save(Employee employee);

    Employee findById(long id);

    void update(Employee employee);

    List<Employee> queryByGender(String gender);

    void delete(long id);

    List<Employee> findAll();

    List<Employee> findWithPagination(int page, int size);
}
