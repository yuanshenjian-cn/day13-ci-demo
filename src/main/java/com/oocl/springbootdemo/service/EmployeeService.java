package com.oocl.springbootdemo.service;

import com.oocl.springbootdemo.repository.EmployeeRepository;
import com.oocl.springbootdemo.repository.entity.Employee;
import com.oocl.springbootdemo.service.dto.CreateEmployeeRequest;
import com.oocl.springbootdemo.service.dto.CreateEmployeeResponse;
import com.oocl.springbootdemo.service.dto.EmployeeResponse;
import com.oocl.springbootdemo.service.dto.UpdateEmployeeRequest;
import com.oocl.springbootdemo.service.exception.EmployeeInactiveException;
import com.oocl.springbootdemo.service.exception.EmployeeNotAmongLegalAgeException;
import com.oocl.springbootdemo.service.exception.EmployeeNotFoundException;
import com.oocl.springbootdemo.service.exception.EmployeeSalaryLimitationException;
import com.oocl.springbootdemo.service.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public CreateEmployeeResponse create(CreateEmployeeRequest request) {
        Employee employee = EmployeeMapper.toEntity(request);
        if (employee.getAge() < 18) {
            throw new EmployeeNotAmongLegalAgeException("Age must be at least 18");
        }
        if (employee.getAge() >= 30 && employee.getSalary() < 20000) {
            throw new EmployeeSalaryLimitationException("Employees over 30 years old with salary below 20000 cannot be created");
        }
        employee.setActive(true);
        employeeRepository.save(employee);

        return new CreateEmployeeResponse(employee.getId());
    }

    @Transactional
    public EmployeeResponse update(long id, UpdateEmployeeRequest updateEmployeeRequest) {
        Employee existedEmployee = employeeRepository.findById(id);
        if (existedEmployee == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        if (!existedEmployee.isActive()) {
            throw new EmployeeInactiveException("Cannot update inactive employee");
        }
        existedEmployee.setName(updateEmployeeRequest.name());
        existedEmployee.setAge(updateEmployeeRequest.age());
        existedEmployee.setSalary(updateEmployeeRequest.salary());

        employeeRepository.update(existedEmployee);
        return EmployeeMapper.toResponse(existedEmployee);
    }

    public EmployeeResponse querySingle(long id) {
        Employee employee = employeeRepository.findById(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        return EmployeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> queryByGender(String gender) {
        List<Employee> employees = employeeRepository.queryByGender(gender);
        return EmployeeMapper.toResponseList(employees);
    }

    public List<EmployeeResponse> queryAll() {
        List<Employee> employees = employeeRepository.findAll();
        return EmployeeMapper.toResponseList(employees);
    }

    public List<EmployeeResponse> queryWithPagination(int page, int size) {
        List<Employee> employees = employeeRepository.findWithPagination(page, size);
        return EmployeeMapper.toResponseList(employees);
    }

    @Transactional
    public void delete(int id) {
        Employee employeeToBeDeleted = employeeRepository.findById(id);
        if (employeeToBeDeleted == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employeeToBeDeleted.setActive(false);
        employeeRepository.update(employeeToBeDeleted);
    }
}
