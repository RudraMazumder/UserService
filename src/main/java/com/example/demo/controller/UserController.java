package com.example.demo.controller;



import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/users")
	public User addUser(@RequestBody User user) {
		userService.saveUser(user);
		return user;
	}
	
	
	@PostMapping("/users/{userName}/roles/{roleName}")
	public void addUserToRole(@PathVariable String userName, @PathVariable String roleName) {
		userService.addUserToRole(userName, roleName);
	}
	
	@GetMapping("/users")
	public List<User> getAllUsers(@RequestParam(required = false) String username){
		if(StringUtils.hasText(username))
			return Arrays.asList(userService.getUserByUserName(username));
		else
			return userService.getAllUsers();
	}
	
}
