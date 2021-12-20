package com.example.demo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
class DemoSpringSecurityApplicationTests {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MockMvc mockMvc;



	@Test
	void GetWithAuthorizedUser() throws Exception {
		if(mockMvc==null)
			log.info("mockMvc is null");
		
		MvcResult mvcResult = mockMvc.perform(formLogin().user("john").password("pwd"))
				.andExpect(authenticated().withUsername("john")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
		mockMvc.perform(get("/api/users").session(session)).andExpect(authenticated()).andExpect(status().isOk());
	}

	@BeforeEach
	public void populateUsers() {
		User user = new User(null, "john", "John Doe", passwordEncoder.encode("pwd"),
				Arrays.asList(new Role(null, "USER")));
		log.info("user {} to be added with role {}", user.getUsername(), user.getRoles());
		userRepo.save(user);
		log.info("user {} added with role {}", user.getUsername(), user.getRoles());

		// user creation with Role ADMIN
		User admin = new User(null, "dave", "David Root", passwordEncoder.encode("pwd"),
				Arrays.asList(new Role(null, "ADMIN")));
		log.info("user {} to be added with role {}", admin.getUsername(), admin.getRoles());
		userRepo.save(admin);
		log.info("user {} added with role {}", admin.getUsername(), admin.getRoles());

		// user creation with Role MANAGER
		User manager = new User(null, "paul", "Paul Davies", passwordEncoder.encode("pwd"),
				Arrays.asList(new Role(null, "MANAGER")));
		log.info("user {} to be added with role {}", manager.getUsername(), manager.getRoles());
		userRepo.save(manager);
		log.info("user {} added with role {}", manager.getUsername(), manager.getRoles());
	}
	
	/*
	 * @Bean(name = "passwordEncoder") public PasswordEncoder encoder() { return new
	 * BCryptPasswordEncoder(); }
	 */

}
