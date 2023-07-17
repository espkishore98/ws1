package com.example.oauthtask.repository;

import com.example.oauthtask.entity.User;
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
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("ram");
        user.setPassword("password");
        userRepository.save(user);

        User foundUser = userRepository.findByUsername("ram");

        assertNotNull(foundUser);
        assertEquals("ram", foundUser.getUsername());
    }
}