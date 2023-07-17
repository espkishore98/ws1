package com.example.oauthtask.controller;

import com.example.oauthtask.entity.User;
import com.example.oauthtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/user")
	public List<User> listUser() {
		return userService.findAll();
	}

	@PostMapping("/user")
	public User create(@RequestBody User user) {
		return userService.save(user);
	}

	@DeleteMapping("/user/{id}")
	public String delete(@PathVariable(value = "id") Long id) {
		userService.delete(id);
		return "success";
	}

}
