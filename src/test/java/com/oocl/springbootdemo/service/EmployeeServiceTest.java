package com.oocl.springbootdemo.service;

import com.oocl.springbootdemo.repository.entity.Employee;
import com.oocl.springbootdemo.service.dto.CreateEmployeeRequest;
import com.oocl.springbootdemo.service.dto.EmployeeResponse;
import com.oocl.springbootdemo.service.dto.UpdateEmployeeRequest;
import com.oocl.springbootdemo.repository.EmployeeRepository;
import com.oocl.springbootdemo.service.exception.EmployeeInactiveException;
import com.oocl.springbootdemo.service.exception.EmployeeNotAmongLegalAgeException;
import com.oocl.springbootdemo.service.exception.EmployeeNotFoundException;
import com.oocl.springbootdemo.service.exception.EmployeeSalaryLimitationException;
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    
    private EmployeeService employeeService;
    
    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
        // Set the private repository field using reflection
        try {
            java.lang.reflect.Field repoField = EmployeeService.class.getDeclaredField("employeeRepository");
            repoField.setAccessible(true);
            repoField.set(employeeService, employeeRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void should_not_create_employee_when_post_given_age_below_18() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("sjyuan", 17, 3000.0, "Male", 0);
        
        assertThrows(EmployeeNotAmongLegalAgeException.class, () -> {
            employeeService.create(request);
        });
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void should_return_single_employee_given_exist_employee_id() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setAge(20);
        employee.setName("sjyuan");
        employee.setGender("Male");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setCompanyId(0);

        when(employeeRepository.findById(1)).thenReturn(employee);

        EmployeeResponse foundEmployee = employeeService.querySingle(1);
        assertEquals(foundEmployee.id(), employee.getId());
        assertEquals(foundEmployee.age(), employee.getAge());
        assertEquals(foundEmployee.name(), employee.getName());

        verify(employeeRepository, times(1)).findById(1);
    }

    @Test
    void should_throw_exception_given_employee_not_existed() {
        when(employeeRepository.findById(1)).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.querySingle(1);
        });
        verify(employeeRepository, times(1)).findById(1);
    }

    @Test
    void should_not_create_employee_when_post_given_age_over_30_and_salary_below_20000() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("sjyuan", 35, 15000.0, "Male", 0);
        
        assertThrows(EmployeeSalaryLimitationException.class, () -> {
            employeeService.create(request);
        });
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void should_create_employee_with_active_true_by_default() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("sjyuan", 25, 30000.0, "Male", 0);
        
        doAnswer(invocation -> {
            Employee arg = invocation.getArgument(0);
            arg.setId(1);
            return null;
        }).when(employeeRepository).save(any(Employee.class));

        employeeService.create(request);

        verify(employeeRepository, times(1)).save(argThat(employee -> 
            employee.isActive() && 
            employee.getName().equals("sjyuan") &&
            employee.getAge() == 25
        ));
    }

    @Test
    void should_not_update_inactive_employee() {
        Employee existingEmployee = new Employee();
        existingEmployee.setId(1);
        existingEmployee.setAge(30);
        existingEmployee.setName("sjyuan");
        existingEmployee.setGender("Male");
        existingEmployee.setActive(false);

        UpdateEmployeeRequest updateEmployee = new UpdateEmployeeRequest("sjyuan updated", 35, 50000.0);

        when(employeeRepository.findById(1)).thenReturn(existingEmployee);

        assertThrows(EmployeeInactiveException.class, () -> {
            employeeService.update(1, updateEmployee);
        });
        verify(employeeRepository, never()).update(any());
    }

    @Test
    void should_perform_soft_delete_when_delete_employee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setAge(30);
        employee.setName("sjyuan");
        employee.setGender("Male");
        employee.setActive(true);

        when(employeeRepository.findById(1)).thenReturn(employee);

        employeeService.delete(1);

        assertEquals(false, employee.isActive());
        verify(employeeRepository, times(1)).update(employee);
        verify(employeeRepository, never()).delete(anyInt());
    }
}