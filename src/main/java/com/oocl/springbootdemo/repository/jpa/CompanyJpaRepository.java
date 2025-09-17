package com.oocl.springbootdemo.repository.jpa;

import com.oocl.springbootdemo.repository.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {

}
