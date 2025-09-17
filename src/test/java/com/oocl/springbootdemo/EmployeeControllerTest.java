package com.oocl.springbootdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyJpaRepository companyJpaRepository;
    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    private long defaultCompanyId;

    @BeforeEach
    void setup() {
        employeeJpaRepository.deleteAll();
        companyJpaRepository.deleteAll();

        Company company = new Company();
        company.setName("Default Company");
        companyJpaRepository.save(company);
        defaultCompanyId = company.getId();
    }


    @Test
    void should_not_create_employee_when_post_given_invalid_employee_age() throws Exception {
        String requestBody = """
                        {
                            "name": "John Smith",
                            "age": 17,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    void should_create_employee_when_post_given_a_valid_body() throws Exception {
        String requestBody = """
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void should_query_employee_when_query_given_a_valid_employee_id() throws Exception {
        long id = createEmployee("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId));

        mockMvc.perform(get("/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.salary").value(60000))
                .andExpect(jsonPath("$.companyId").value(defaultCompanyId));
    }

    private long createEmployee(String requestBody) throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        MvcResult mvcResult = resultActions.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        return new ObjectMapper().readTree(contentAsString).get("id").asLong();
    }

    @Test
    void should_update_employee_when_update_given_a_valid_employee() throws Exception {
        Company company = new Company();
        company.setName("oocl");
        companyJpaRepository.save(company);

        Employee employee = new Employee();
        employee.setSalary(800000);
        employee.setGender("Male");
        employee.setName("sjyuan");
        employee.setActive(true);
        employee.setCompanyId(company.getId());
        employeeJpaRepository.save(employee);

        String updateRequestBody = """
                        {
                            "name": "John Smith updated",
                            "age": 35,
                            "salary": 90000
                        }
                """.formatted(employee.getId());

        mockMvc.perform(put("/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Smith updated"))
                .andExpect(jsonPath("$.age").value(35))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.salary").value(90000));
    }

    @Test
    void should_return_male_employee_when_query_given_valid_male() throws Exception {
        long id = createEmployee("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId));

        mockMvc.perform(get("/employees?gender=male")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[0].age").value(30))
                .andExpect(jsonPath("$[0].gender").value("MALE"))
                .andExpect(jsonPath("$[0].salary").value(60000))
                .andExpect(jsonPath("$[0].companyId").value(defaultCompanyId));
    }

    @Test
    void should_delete_employee() throws Exception {
        String requestBody = """
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        long id = createEmployee(requestBody);


        mockMvc.perform(delete("/employees/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void should_not_create_employee_when_age_over_30_and_salary_below_20000() throws Exception {
        String requestBody = """
                        {
                            "name": "John Smith",
                            "age": 35,
                            "gender": "MALE",
                            "salary": 15000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_create_employee_with_active_true_by_default() throws Exception {
        long id = createEmployee("""
                        {
                            "name": "John Smith",
                            "age": 25,
                            "gender": "MALE",
                            "salary": 30000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId));

        mockMvc.perform(get("/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void should_not_update_inactive_employee() throws Exception {
        long id = createEmployee("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId));

        mockMvc.perform(delete("/employees/{id}", id));

        String updateRequestBody = """
                        {
                            "name": "John Smith updated",
                            "age": 35,
                            "salary": 80000
                        }
                """;

        mockMvc.perform(put("/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_get_all_employees() throws Exception {
        String requestBody1 = """
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": 60000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        String requestBody2 = """
                        {
                            "name": "Jane Doe",
                            "age": 25,
                            "gender": "FEMALE",
                            "salary": 55000,
                            "companyId": %d
                        }
                """.formatted(defaultCompanyId);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody1));

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    void should_get_employees_with_pagination() throws Exception {
        for (int i = 1; i <= 10; i++) {
            createEmployee(String.format("""
                            {
                                "name": "Employee %d",
                                "age": %d,
                                "gender": "MALE",
                                "salary": 50000,
                                "companyId": %d
                            }
                    """, i, 20 + i, defaultCompanyId));
        }

        mockMvc.perform(get("/employees?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Employee 1"))
                .andExpect(jsonPath("$[4].name").value("Employee 5"));

        mockMvc.perform(get("/employees?page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Employee 6"))
                .andExpect(jsonPath("$[4].name").value("Employee 10"));
    }

    @Test
    void should_return_validation_error_when_gender_is_null() throws Exception {
        String requestBody = String.format("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": null,
                            "salary": 60000,
                            "companyId": %d
                        }
                """, defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.gender").value("Gender cannot be null"));
    }

    @Test
    void should_return_validation_error_when_salary_is_negative() throws Exception {
        String requestBody = String.format("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": "MALE",
                            "salary": -1000,
                            "companyId": %d
                        }
                """, defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.salary").value("Salary must be positive number"));
    }

    @Test
    void should_return_multiple_validation_errors() throws Exception {
        String requestBody = String.format("""
                        {
                            "name": "John Smith",
                            "age": 30,
                            "gender": null,
                            "salary": -1000,
                            "companyId": %d
                        }
                """, defaultCompanyId);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.gender").value("Gender cannot be null"))
                .andExpect(jsonPath("$.salary").value("Salary must be positive number"));
    }

}