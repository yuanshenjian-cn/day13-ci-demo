package com.oocl.springbootdemo.repository;

import com.oocl.springbootdemo.repository.entity.Employee;

import java.util.ArrayList;
import java.util.List;

//@Repository
public class EmployeeRepositoryInmemoryImpl implements EmployeeRepository {
    private final List<Employee> employees = new ArrayList<>();

    @Override
    public void save(Employee employee) {
        employee.setId(employees.size() + 1);
        employees.add(employee);
    }

    @Override
    public Employee findById(long id) {
        return employees.stream().filter(employee -> employee.getId() == id).findFirst()
                .orElse(null);
    }

    @Override
    public void update(Employee employee) {
        Employee existed = findById(employee.getId());
        if (existed != null) {
            existed.setAge(employee.getAge());
            existed.setGender(employee.getGender());
            existed.setName(employee.getName());
            existed.setSalary(employee.getSalary());
            existed.setActive(employee.isActive());
        }
    }

    @Override
    public List<Employee> queryByGender(String gender) {
        return employees.stream().filter(employee -> employee.getGender().equalsIgnoreCase(gender)).toList();
    }

    @Override
    public void delete(long id) {
        Employee employee = findById(id);
        if (employee != null) {
            employees.remove(employee);
        }
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    @Override
    public List<Employee> findWithPagination(int page, int size) {
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, employees.size());

        if (startIndex >= employees.size()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(employees.subList(startIndex, endIndex));
    }
}