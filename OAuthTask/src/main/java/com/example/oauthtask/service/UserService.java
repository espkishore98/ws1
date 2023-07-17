package com.example.oauthtask.service;

import com.example.oauthtask.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);
    List<User> findAll();
    void delete(long id);
}
