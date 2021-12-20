package com.example.demo;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

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

		MvcResult mvcResult = mockMvc.perform(formLogin().user("john").password("pwd"))
				.andExpect(authenticated().withUsername("john")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
		mockMvc.perform(get("/api/users").session(session)).andExpect(authenticated()).andExpect(status().isOk());
	}

	@Test
	void GetWithUnAuthorizedUser() throws Exception {

		MvcResult mvcResult = mockMvc.perform(formLogin().user("paul").password("pwd"))
				.andExpect(authenticated().withUsername("paul")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
		mockMvc.perform(get("/api/users").session(session)).andExpect(authenticated())
				.andExpect(status().isForbidden());
	}

	@Test
	void GetWithInvalidUser() throws Exception {
		mockMvc.perform(formLogin().user("tim").password("pwd")).andExpect(unauthenticated()).andReturn();

	}

	@Test
	void CreateANewUserWithAuthorizedUser() throws Exception {
		MvcResult mvcResult = mockMvc.perform(formLogin().user("dave").password("pwd"))
				.andExpect(authenticated().withUsername("dave")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);

		User user = new User(null, "ian", "ian kramer", passwordEncoder.encode("pwd"),
				Arrays.asList(new Role(null, "USER")));

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(user);
		
	    mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
	            .content(requestJson).session(session))
	            .andExpect(status().isOk());

	}
	
	@Test
	void CreateANewUserWithUnAuthorizedUser() throws Exception {
		MvcResult mvcResult = mockMvc.perform(formLogin().user("john").password("pwd"))
				.andExpect(authenticated().withUsername("john")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);

		User user = new User(null, "ian", "ian kramer", passwordEncoder.encode("pwd"),
				Arrays.asList(new Role(null, "USER")));

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(user);
		
	    mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
	            .content(requestJson).session(session))
	            .andExpect(status().isForbidden());

	}
	
	@Test
	void GetUserByUserName() throws Exception {
		MvcResult mvcResult = mockMvc.perform(formLogin().user("john").password("pwd"))
				.andExpect(authenticated().withUsername("john")).andReturn();

		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);

			
		mockMvc.perform(get("/api/users?username=john").session(session)).andExpect(authenticated())
		.andExpect(status().isOk());

	}
	
	@Test
	void addUserToRole() throws Exception {
		MvcResult mvcResult = mockMvc.perform(formLogin().user("john").password("pwd"))
				.andExpect(authenticated().withUsername("john")).andReturn();
		MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
		mockMvc.perform(post("/api/users/john/roles/MANAGER").session(session)).andExpect(authenticated())
		.andExpect(status().isOk());
		
		mockMvc.perform(formLogin().user("john").password("pwd"))
		.andExpect(authenticated().withUsername("john").withAuthorities(Arrays.asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("MANAGER"))));
		
		
	}

	@BeforeEach
	public void populateUsers() {
		User userEntity = userRepo.getUserByUsername("john");

		if (userEntity == null) {

			User user = new User(null, "john", "John Doe", passwordEncoder.encode("pwd"),
					Arrays.asList(new Role(null, "USER")));

			log.info("user {} to be added with role {}", user.getUsername(), user.getRoles());
			userRepo.save(user);
			log.info("user {} added with role {}", user.getUsername(), user.getRoles());

		}
		// user creation with Role ADMIN

		User daveEntity = userRepo.getUserByUsername("dave");

		if (daveEntity == null) {

			User admin = new User(null, "dave", "David Root", passwordEncoder.encode("pwd"),
					Arrays.asList(new Role(null, "ADMIN")));
			log.info("user {} to be added with role {}", admin.getUsername(), admin.getRoles());
			userRepo.save(admin);
			log.info("user {} added with role {}", admin.getUsername(), admin.getRoles());
		}
		// user creation with Role MANAGER

		User paulEntity = userRepo.getUserByUsername("paul");

		if (paulEntity == null) {
			User manager = new User(null, "paul", "Paul Davies", passwordEncoder.encode("pwd"),
					Arrays.asList(new Role(null, "MANAGER")));
			log.info("user {} to be added with role {}", manager.getUsername(), manager.getRoles());
			userRepo.save(manager);
			log.info("user {} added with role {}", manager.getUsername(), manager.getRoles());
		}

	}


}
