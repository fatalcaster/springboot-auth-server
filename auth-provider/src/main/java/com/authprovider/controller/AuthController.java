package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.config.UrlTracker;
import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserCredDTO;
import com.authprovider.dto.UserDTO;
import com.authprovider.dto.internal.Keystore;
import com.authprovider.exceptions.Forbidden;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.NotAuthorized;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.User;
import com.authprovider.service.TokenService;
import com.authprovider.service.UserService;
import com.authprovider.service.jwt.JwtPayload;
import com.authprovider.service.jwt.JwtService;
import com.authprovider.service.jwt.TokenPair;
import com.authprovider.service.jwt.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@CrossOrigin(origins = "accounts.codedepo.com", allowCredentials = "true")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

  private static final Logger LOG = LoggerFactory.getLogger(
    AuthController.class
  );

  @Autowired
  private UserService service;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private AuthUtil authUtil;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public String registerUser(
    @Valid @RequestBody(required = true) UserCredDTO user
  ) {
    User newUser = user.toUser();

    try {
      service.saveUser(newUser);
    } catch (Error e) {
      LOG.error(e.toString());
    }
    return newUser.getUsername();
  }

  @PostMapping("/login")
  public UserDTO loginUser(
    @Valid @RequestBody(required = true) UserCredDTO userDto,
    HttpServletResponse response
  ) {
    User user = service
      .getUserByEmail(userDto.getEmail())
      .orElseThrow(UserNotFound::new);

    LOG.info(user.toString());

    if (!service.passwordsMatch(user, userDto.getPassword())) {
      throw new BadCredentialsException("Invalid credentials");
    }

    if (
      !user.isEnabled() ||
      !user.isAccountNonExpired() ||
      !user.isAccountNonLocked() ||
      !user.isCredentialsNonExpired()
    ) {
      throw new NotAuthorized();
    }

    UserDTO userResDto = user.toResponseDTO();
    try {
      TokenPair tokens = jwtService.createTokenPair(userResDto);

      final ResponseCookie refreshCookie = SecureCookie.build(
        SecureCookie.REFRESH_TOKEN_KEY,
        tokens.getRefreshToken()
      );

      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

      return userResDto;
    } catch (Exception e) {
      System.out.println(e);
      throw new InternalError();
    }
  }

  @ResponseStatus(code = HttpStatus.OK)
  @PostMapping("/refresh")
  public String refreshToken(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    Cookie cookie = WebUtils.getCookie(request, SecureCookie.REFRESH_TOKEN_KEY);
    if (cookie == null) {
      throw new NotAuthorized("No cookie");
    }

    Keystore keystore = authUtil.parseKeystore(cookie.getValue()).orElse(null);

    if (keystore == null) {
      throw new Forbidden();
    }

    JwtPayload jwtPayload = JwtPayload
      .buildFromToken(
        cookie.getValue(),
        keystore.getPublicKey(),
        UrlTracker.issuer
      )
      .orElseThrow(() -> new NotAuthorized("Invalid JWT"));

    // todo: check if the db contains the token that has been revoked

    try {
      jwtPayload.setTokenType(TokenType.ACCESS_TOKEN);
      return jwtPayload.issueAndConvertToToken(jwtPayload, keystore);
    } catch (Exception e) {
      System.err.println(e);
      throw new InternalError();
    }
  }
}
