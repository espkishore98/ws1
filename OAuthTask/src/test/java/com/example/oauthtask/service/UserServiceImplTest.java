package com.example.oauthtask.service;

import com.example.oauthtask.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import com.example.oauthtask.entity.User;
import com.example.oauthtask.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void testLoadUserByUsername() {
        String username = "ram";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(new HashSet<>(Arrays.asList("ADMIN")));

        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertEquals(1, userDetails.getAuthorities().size());
       verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsername_Exception() {
        String username = "ram";

        when(userRepository.findByUsername(username)).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
       // userService.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testFindAll() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());

        when(userRepository.findAll()).thenReturn(userList);

        List<User> allUsers = userService.findAll();

        assertEquals(2, allUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testDelete() {
        long id = 1L;

        userService.delete(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void testSave() {
        User user = new User();
        user.setUsername("ram");
        user.setPassword("password");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertEquals(user, savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSave_Failure() {
        User user = new User();
        user.setUsername("ram");
        user.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> {
            userService.save(user);
        });
       // userService.save(user);
    }
}
