package com.authprovider.controller;

import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserCredDTO;
import com.authprovider.dto.UserDTO;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.Unauthorized;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.User;
import com.authprovider.service.UserService;
import com.authprovider.service.jwt.JwtService;
import com.authprovider.service.jwt.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

  @Autowired
  private UserService service;

  @Autowired
  private JwtService jwtService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public String registerUser(
    @Valid @RequestBody(required = true) UserCredDTO user
  ) {
    User newUser = user.toUser();

    service.saveUser(newUser);
    return newUser.getUsername();
  }

  @PostMapping("/login")
  public UserDTO loginUser(
    @Valid @RequestBody(required = true) UserCredDTO userDto,
    @RequestParam Optional<String> redirectUri,
    HttpServletResponse response
  ) {
    User user = service
      .getUserByEmail(userDto.getEmail())
      .orElseThrow(UserNotFound::new);

    if (!service.passwordsMatch(user, userDto.getPassword())) {
      throw new BadCredentialsException("Bad request");
    }

    if (
      !user.isEnabled() ||
      !user.isAccountNonExpired() ||
      !user.isAccountNonLocked() ||
      !user.isCredentialsNonExpired()
    ) {
      throw new Unauthorized();
    }

    UserDTO userResDto = user.toResponseDTO();

    try {
      TokenPair tokens = jwtService.createTokenPair(userResDto);

      SecureCookie accessCookie = new SecureCookie(
        SecureCookie.accessTokenKey,
        tokens.getAccessToken()
      );
      response.addCookie(accessCookie);

      SecureCookie refreshCookie = new SecureCookie(
        SecureCookie.refreshTokenKey,
        tokens.getRefreshToken()
      );
      response.addCookie(accessCookie);
      response.addCookie(refreshCookie);

      if (redirectUri.isPresent()) {
        String redirectLocation = redirectUri.orElseThrow(InternalError::new);
        response.sendRedirect(redirectLocation);
      }
      return userResDto;
    } catch (Exception e) {
      System.err.println(e);
      throw new InternalError();
    }
  }

  @ResponseStatus(code = HttpStatus.OK)
  @PostMapping("/logout")
  public void logoutUser(HttpServletResponse response) {
    SecureCookie cookie = new SecureCookie(SecureCookie.accessTokenKey, "")
      .delete();
    response.addCookie(cookie);
  }
}
