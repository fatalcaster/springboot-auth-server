package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.dto.UserDTO;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.User;
import com.authprovider.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "accounts.codedepo.com", allowCredentials = "true")
@RequestMapping(value = "/api/user")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  AuthUtil authUtil;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/all")
  public List<UserDTO> getUsers(@RequestParam int page) {
    List<User> users = userService.getAllUsers(page);

    List<UserDTO> usersDto = new LinkedList<>();
    users.forEach(user -> usersDto.add(user.toResponseDTO()));
    return usersDto;
  }

  @GetMapping("/me")
  public UserDTO getMe(HttpServletRequest request) {
    UserDTO userDto = authUtil.extractUser(request);
    User user = userService
      .getUserById(userDto.getId())
      .orElseThrow(UserNotFound::new);
    return user.toResponseDTO();
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/role/{userId}")
  public void setRole(
    @PathVariable(name = "userId", required = true) String userId,
    @RequestParam String role
  ) {
    userService.addRole(userId, role);
  }
}
