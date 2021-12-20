package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

	@Override
	public User saveUser(User user) {

		String passwd = user.getPassword();
		String encodedPwd = passwordEncoder.encode(passwd);
		user.setPassword(encodedPwd);
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserDetails userDetails = null;
		User user = userRepo.getUserByUsername(username);
		if (user == null) {
			log.error("User with username {} not found", username);
			throw new UsernameNotFoundException("Username not found");
		} else {

			List<SimpleGrantedAuthority> ga = new ArrayList<SimpleGrantedAuthority>();
			user.getRoles().stream().forEach(t -> {
				SimpleGrantedAuthority sga = new SimpleGrantedAuthority(t.getName());
				ga.add(sga);
			});
			userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
					ga);
		}

		return userDetails;
	}

}
