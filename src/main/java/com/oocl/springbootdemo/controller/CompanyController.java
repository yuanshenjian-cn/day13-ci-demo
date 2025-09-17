package com.oocl.springbootdemo.controller;

import com.oocl.springbootdemo.service.CompanyService;
import com.oocl.springbootdemo.service.dto.CompanyResponse;
import com.oocl.springbootdemo.service.dto.CreateCompanyRequest;
import com.oocl.springbootdemo.service.dto.CreateCompanyResponse;
import com.oocl.springbootdemo.service.dto.UpdateCompanyRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public List<CompanyResponse> queryCompanies(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page != null && size != null) {
            return companyService.queryWithPagination(page, size);
        } else {
            return companyService.queryAll();
        }
    }

    @GetMapping("/companies/{id}")
    public CompanyResponse getCompany(@PathVariable long id) {
        return companyService.querySingle(id);
    }

    @PostMapping("/companies")
    public ResponseEntity<CreateCompanyResponse> createCompany(@RequestBody CreateCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request));
    }

    @PutMapping("/companies/{id}")
    public CompanyResponse updateCompany(@PathVariable long id, @RequestBody UpdateCompanyRequest request) {
        return companyService.update(id, request);
    }

    @DeleteMapping("/companies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable long id) {
        companyService.delete(id);
    }
}
