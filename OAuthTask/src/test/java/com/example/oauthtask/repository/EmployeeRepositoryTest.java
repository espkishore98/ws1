package com.example.oauthtask.repository;

import com.example.oauthtask.entity.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {
    @Autowired
    EmployeeRepository employeeRepository;
    @Test
    void testFindById() {
        Iterable<Employee> employees = employeeRepository.findAll();
        Assertions.assertThat(employees).extracting(Employee::getName);
        System.out.println("size :: "+ employeeRepository.findAll().size());
    }
}