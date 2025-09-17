package com.oocl.springbootdemo;

import com.oocl.springbootdemo.repository.entity.Company;
import com.oocl.springbootdemo.repository.entity.Employee;
import com.oocl.springbootdemo.repository.jpa.CompanyJpaRepository;
import com.oocl.springbootdemo.repository.jpa.EmployeeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyJpaRepository companyJpaRepository;

    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    @BeforeEach
    void setup() {
        companyJpaRepository.deleteAll();
    }

    @Test
    void should_return_company_with_employees() throws Exception {
        Company company = new Company();
        company.setName("oocl");
        companyJpaRepository.save(company);

        Employee employee = new Employee();
        employee.setSalary(800000);
        employee.setGender("Male");
        employee.setName("sjyuan");
        employee.setCompanyId(company.getId());
        employeeJpaRepository.save(employee);


        mockMvc.perform(get("/companies/{id}", company.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.employees.length()").value(1));
    }

    @Test
    void should_create_company_when_post_given_a_valid_body() throws Exception {
        String requestBody = """
                        {
                            "name": "OOCL"
                        }
                """;

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void should_query_company_when_query_given_a_valid_company_id() throws Exception {
        String requestBody = """
                        {
                            "name": "OOCL"
                        }
                """;

        MvcResult createResult = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        long companyId = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseContent).get("id").asLong();

        mockMvc.perform(get("/companies/{id}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(companyId))
                .andExpect(jsonPath("$.name").value("OOCL"));
    }

    @Test
    void should_update_company_when_update_given_a_valid_company() throws Exception {
        String requestBody = """
                        {
                            "name": "OOCL"
                        }
                """;

        MvcResult createResult = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        long companyId = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseContent).get("id").asLong();

        String updateRequestBody = """
                        {
                            "name": "OOCL Updated"
                        }
                """;

        mockMvc.perform(put("/companies/{id}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(companyId))
                .andExpect(jsonPath("$.name").value("OOCL Updated"));
    }

    @Test
    void should_delete_company() throws Exception {
        String requestBody = """
                        {
                            "name": "OOCL"
                        }
                """;

        MvcResult createResult = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        long companyId = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseContent).get("id").asLong();

        mockMvc.perform(delete("/companies/" + companyId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/companies/" + companyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_get_all_companies() throws Exception {
        String requestBody1 = """
                        {
                            "name": "OOCL"
                        }
                """;

        String requestBody2 = """
                        {
                            "name": "Spring"
                        }
                """;

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1));

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2));

        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("OOCL"))
                .andExpect(jsonPath("$[1].name").value("Spring"));
    }

    @Test
    void should_get_companies_with_pagination() throws Exception {
        for (int i = 1; i <= 10; i++) {
            String requestBody = String.format("""
                            {
                                "name": "Company %d"
                            }
                    """, i);

            mockMvc.perform(post("/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
        }

        mockMvc.perform(get("/companies?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Company 1"))
                .andExpect(jsonPath("$[4].name").value("Company 5"));

        mockMvc.perform(get("/companies?page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Company 6"))
                .andExpect(jsonPath("$[4].name").value("Company 10"));
    }
}
