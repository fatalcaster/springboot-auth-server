package com.authprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.authprovider.model.Role;
import com.authprovider.model.User;
import com.authprovider.service.RoleService;
import com.authprovider.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  UserService userService;

  @Autowired
  RoleService roleService;

  @BeforeAll
  public void setup() {
    Role role = new Role();
    role.setRole("user");
    roleService.saveRole(role);
  }

  @Test
  public void shouldRegisterUser() throws Exception {
    Object req = new Object() {
      public String email = "test@test.com";
      public String password = "test";
    };

    ObjectMapper objectMapper = new ObjectMapper();
    String reqContent = objectMapper.writeValueAsString(req);

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/user/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(reqContent)
      )
      .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void shouldNotFindTheUser_403() throws Exception {
    String emailEg = UUID.randomUUID().toString().substring(0, 5) + "@test.com";
    Object req = new Object() {
      public String email = emailEg;
      public String password = "test";
    };

    ObjectMapper objectMapper = new ObjectMapper();
    String reqContent = objectMapper.writeValueAsString(req);

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/user/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(reqContent)
      )
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  public void shouldLogin() throws Exception {
    String emailEg = UUID.randomUUID().toString().substring(0, 5) + "@test.com";
    final String passwordEg = "test";
    User user = new User(emailEg, passwordEg);

    userService.saveUser(user);

    assertEquals(true, userService.getUserByEmail(emailEg).isPresent());

    Object req = new Object() {
      public String email = emailEg;
      public String password = passwordEg;
    };

    ObjectMapper objectMapper = new ObjectMapper();
    String reqContent = objectMapper.writeValueAsString(req);

    // when
    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/user/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(reqContent)
      )
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
  }
}
