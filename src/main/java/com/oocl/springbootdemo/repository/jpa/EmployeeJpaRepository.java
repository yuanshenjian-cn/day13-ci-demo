package com.oocl.springbootdemo.repository.jpa;

import com.oocl.springbootdemo.repository.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeJpaRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByGenderIgnoreCase(String gender);
}
