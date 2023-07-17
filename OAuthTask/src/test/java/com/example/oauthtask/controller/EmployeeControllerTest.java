package com.example.oauthtask.controller;

import com.example.oauthtask.dto.EmployeeDTO;
import com.example.oauthtask.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static MockMvc mockMvc;
    @BeforeEach
    public void setup()
    {
        if(mockMvc == null)
        {
            synchronized(EmployeeControllerTest.class) {
                if(mockMvc == null)
                {
                    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
                }
            }
        }


    }



    @Test
    @DisplayName("Test Should Add the Employee Detail")
    @Order(1)
    void testAddEmployee() throws Exception {
        EmployeeDTO emp = new EmployeeDTO(1L, "Sai", "ra@gmail.com", "TA");
        ObjectMapper objMap = new ObjectMapper();
        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMap.writeValueAsString(emp)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


    @Test
    @WithMockUser(roles = { "ADMIN", "USER" })
    public void testGetAllEmployees_Success() throws Exception {
        List<EmployeeDTO> employeeDTOs = Arrays.asList(
                new EmployeeDTO(1L, "Sai", "ra@gmail.com", "TA"),
                new EmployeeDTO(2L, "Ram", "ra@gmail.com", "TA")
        );
        when(employeeService.getAllEmployees()).thenReturn(employeeDTOs);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8089/employees/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
              .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Ram"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllEmployees_Forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8089/employees/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testGetAllEmployees_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8089/employees/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    void getEmployeeById() throws Exception {
        mockMvc.perform(get("http://localhost:8089/employees/getById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sai"));
    }

    @Test
    @DisplayName("Test Should Return Not Found for Non-existing Employee")
    void testGetEmployeeByIdNotFound() throws Exception {
        long nonExistingId = 100L;

        mockMvc.perform(MockMvcRequestBuilders.get("/getById/{id}", nonExistingId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
}
