package com.oocl.springbootdemo.controller;

import com.oocl.springbootdemo.service.EmployeeService;
import com.oocl.springbootdemo.service.dto.CreateEmployeeRequest;
import com.oocl.springbootdemo.service.dto.CreateEmployeeResponse;
import com.oocl.springbootdemo.service.dto.EmployeeResponse;
import com.oocl.springbootdemo.service.dto.UpdateEmployeeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employees")
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PutMapping("/employees/{id}")
    public EmployeeResponse updateEmployee(@PathVariable long id, @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        return employeeService.update(id, updateEmployeeRequest);
    }

    @GetMapping("/employees/{id}")
    public EmployeeResponse getEmployee(@PathVariable long id) {
        return employeeService.querySingle(id);

    }

    @GetMapping("/employees")
    public List<EmployeeResponse> queryEmployees(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page != null && size != null) {
            return employeeService.queryWithPagination(page, size);
        }
        if (gender != null) {
            return employeeService.queryByGender(gender);
        }
        return employeeService.queryAll();
    }

    @DeleteMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable int id) {
        employeeService.delete(id);
    }
}
