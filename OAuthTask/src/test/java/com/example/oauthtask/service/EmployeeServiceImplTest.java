package com.example.oauthtask.service;
import static org.mockito.ArgumentMatchers.any;
import com.example.oauthtask.dto.EmployeeDTO;
import com.example.oauthtask.entity.Employee;
import com.example.oauthtask.exception.EmployeeNotFoundException;
import com.example.oauthtask.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @InjectMocks
    EmployeeServiceImpl employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Test
    public void testAddEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setName("John Doe");
        employeeDTO.setEmailId("john.doe@example.com");
        employeeDTO.setDesignation("Developer");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmailId("john.doe@example.com");
        employee.setDesignation("Developer");

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO addedEmployee = employeeService.addEmployee(employeeDTO);

        assertEquals(employeeDTO, addedEmployee);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testGetAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(1L, "Ram", "ram@gmail.com", "Developer"));
        employeeList.add(new Employee(2L, "Krish", "krish@gmail.com", "Manager"));

        when(employeeRepository.findAll()).thenReturn(employeeList);

        List<EmployeeDTO> allEmployees = employeeService.getAllEmployees();

        assertNotNull(allEmployees);
        assertEquals(2, allEmployees.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testGetEmployeeById() {
        Long id = 1L;
        Employee employee = new Employee(id, "Ram", "ram@gmail.com", "Developer");

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        EmployeeDTO foundEmployee = employeeService.getEmployeeById(id);

        assertNotNull(foundEmployee);
        assertEquals(employee.getName(), foundEmployee.getName());
        assertEquals(employee.getEmailId(), foundEmployee.getEmailId());
        assertEquals(employee.getDesignation(), foundEmployee.getDesignation());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    public void testGetEmployeeById_Exception() {
        Long id = 1L;

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(id);
        });

        verify(employeeRepository, times(1)).findById(id);
    }
}