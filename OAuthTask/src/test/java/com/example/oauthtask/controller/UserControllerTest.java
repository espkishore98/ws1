package com.example.oauthtask.controller;

import com.example.oauthtask.entity.User;
import com.example.oauthtask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Mock
    private UserService userService;
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
    @Order(2)
    @DisplayName("Test Should Return List of Users")
    void testListUser() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("dummy");
        user1.setPassword("pass");
        user1.setRoles(new HashSet<>(Arrays.asList("ADMIN")));
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("dummy");
        user2.setPassword("pass");
        user2.setRoles(new HashSet<>(Arrays.asList("ADMIN")));
        List<User> userList = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    @Order(1)
    @DisplayName("Test Should Create User")
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("dummy1");
        user.setPassword("pass");
        user.setRoles(new HashSet<>(Arrays.asList("ADMIN")));

        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"username\":\"name\",\"password\":\"pass\",\"roles\":[\"ADMIN\"]}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(3)
    public void testDelete() throws Exception {


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/user/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

    }




}