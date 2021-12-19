package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.User;

public interface UserService {
	
	public List<User> getAllUsers();
	public User saveUser(User user);
	public User getUserByUserName(String userName);
	public void addUserToRole(String userName, String roleName);

}
