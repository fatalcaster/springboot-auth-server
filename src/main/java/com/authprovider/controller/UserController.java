package com.authprovider.controller;

import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserRequestDTO;
import com.authprovider.dto.UserResponseDTO;
import com.authprovider.model.User;
import com.authprovider.service.JWTService;
import com.authprovider.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/user")
@RestController
public class UserController {

  @Autowired
  private UserService service;

  @Autowired
  private JWTService jwtService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public String registerUser(
    @Valid @RequestBody(required = true) UserRequestDTO user
  ) {
    User newUser = user.toUser();

    service.saveUser(newUser);
    return newUser.getUsername();
  }

  @PostMapping("/login")
  public UserResponseDTO loginUser(
    @Valid @RequestBody(required = true) UserRequestDTO userDto,
    HttpServletResponse response
  ) {
    User user = service.getUserByEmail(userDto.getEmail()).orElse(null);

    if (user == null) {
      throw new AuthenticationCredentialsNotFoundException(
        "User with that email doesn't exist"
      );
    }

    if (
      Boolean.FALSE.equals(service.passwordsMatch(user, userDto.getPassword()))
    ) {
      throw new BadCredentialsException("Bad request");
    }

    UserResponseDTO userResDto = user.toResponseDTO();

    String accessToken = jwtService.generateToken(userResDto);

    SecureCookie cookie = new SecureCookie("access_token", accessToken);
    response.addCookie(cookie);
    return userResDto;
  }

  @GetMapping("/all")
  public List<User> getUsers() {
    return service.getAllUsers();
  }
}
