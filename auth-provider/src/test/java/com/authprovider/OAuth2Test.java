package com.authprovider;

import com.authprovider.model.Client;
import com.authprovider.model.Role;
import com.authprovider.model.User;
import com.authprovider.service.ClientService;
import com.authprovider.service.RoleService;
import com.authprovider.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OAuth2Test {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  UserService userService;

  @Autowired
  RoleService roleService;

  @Autowired
  ClientService clientService;

  @Autowired
  PasswordEncoder passwordEncoder;

  User user;
  Client client;
  String clientSecret;

  @BeforeAll
  public void setup() {
    Role role = new Role();
    role.setRole("user");
    try {
      roleService.saveRole(role);
    } catch (Exception e) {}

    user = new User("test1@test.com", "password");
    userService.saveUser(user);

    client = new Client();
    client.setOwner(user);
    // client = clientService.saveClient(client);
  }

  @Test
  public void login() throws Exception {
    Object cred = new Object() {
      public String email = "test1@test.com";
      public String password = "password";
    };
    ObjectMapper objectMapper = new ObjectMapper();
    String reqContent = objectMapper.writeValueAsString(cred);
    mockMvc.perform(
      MockMvcRequestBuilders
        .post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(reqContent)
    );
  }
}
