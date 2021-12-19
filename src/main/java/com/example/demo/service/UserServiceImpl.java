package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Transactional
@Slf4j
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

	@Override
	public User saveUser(User user) {
		return userRepo.save(user);
	}

	@Override
	public User getUserByUserName(String userName) {
		return userRepo.getUserByUsername(userName);
	}

	@Override
	public void addUserToRole(String userName, String roleName) {
		userRepo.getUserByUsername(userName).getRoles().add(roleRepo.getRoleByName(roleName));
		
	}
	
	@PostConstruct
	public void setup() {
		
		User user = new User(null, "john", "John Doe", Arrays.asList(new Role(null, "User")));
		log.info("user {} to be added", user.getUsername());
		userRepo.save(user);
		log.info("user {} added", user.getUsername());
	}

}
