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
	
	/*
	 * @PostConstruct public void populateUser() {
	 * 
	 * User userEntity = userRepo.getUserByUsername("john");
	 * 
	 * if (userEntity == null) {
	 * 
	 * User user = new User(null, "john", "John Doe", passwordEncoder.encode("pwd"),
	 * Arrays.asList(new Role(null, "USER")));
	 * 
	 * log.info("user {} to be added with role {}", user.getUsername(),
	 * user.getRoles()); userRepo.save(user); log.info("user {} added with role {}",
	 * user.getUsername(), user.getRoles());
	 * 
	 * } // user creation with Role ADMIN
	 * 
	 * User daveEntity = userRepo.getUserByUsername("dave");
	 * 
	 * if (daveEntity == null) {
	 * 
	 * User admin = new User(null, "dave", "David Root",
	 * passwordEncoder.encode("pwd"), Arrays.asList(new Role(null, "ADMIN")));
	 * log.info("user {} to be added with role {}", admin.getUsername(),
	 * admin.getRoles()); userRepo.save(admin);
	 * log.info("user {} added with role {}", admin.getUsername(),
	 * admin.getRoles()); } // user creation with Role MANAGER
	 * 
	 * User paulEntity = userRepo.getUserByUsername("paul");
	 * 
	 * if (paulEntity == null) { User manager = new User(null, "paul",
	 * "Paul Davies", passwordEncoder.encode("pwd"), Arrays.asList(new Role(null,
	 * "MANAGER"))); log.info("user {} to be added with role {}",
	 * manager.getUsername(), manager.getRoles()); userRepo.save(manager);
	 * log.info("user {} added with role {}", manager.getUsername(),
	 * manager.getRoles()); }
	 * 
	 * }
	 */

}
