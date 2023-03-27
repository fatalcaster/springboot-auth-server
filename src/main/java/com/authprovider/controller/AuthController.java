package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.config.UrlTracker;
import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserCredDTO;
import com.authprovider.dto.UserDTO;
import com.authprovider.dto.internal.Keystore;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

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

      SecureCookie refreshCookie = new SecureCookie(
        SecureCookie.refreshTokenKey,
        tokens.getRefreshToken()
      );
      SecureCookie accessCookie = new SecureCookie(
        SecureCookie.accessTokenKey,
        tokens.getAccessToken()
      );

      response.addCookie(accessCookie);
      response.addCookie(refreshCookie);
      response.addHeader("Set-Cookie", "key=value; HttpOnly; SameSite=Strict");
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
  @GetMapping("/refresh")
  public void refreshToken(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    SecureCookie cookie = (SecureCookie) WebUtils.getCookie(
      request,
      SecureCookie.refreshTokenKey
    );

    if (cookie == null) {
      throw new NotAuthorized();
    }

    Keystore keystore = authUtil.parseKeystore(cookie.getValue()).orElse(null);

    if (keystore == null) {
      response.addCookie(SecureCookie.delete(SecureCookie.refreshTokenKey));
      throw new NotAuthorized();
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
      SecureCookie accessCokie = new SecureCookie(
        SecureCookie.accessTokenKey,
        jwtPayload.issueAndConvertToToken(jwtPayload, keystore)
      );
      response.addCookie(accessCokie);
    } catch (Exception e) {
      System.err.println(e);
      throw new InternalError();
    }
  }
}
//   @ResponseStatus(code = HttpStatus.OK)
//   @PostMapping("/logout")
//   public void logoutUser(
//     HttpServletRequest request,
//     HttpServletResponse response
//   ) {
//     for (Cookie cookie : request.getCookies()) {
//       System.out.println(cookie.getName());
//       String cookieName = cookie.getName();
//       Cookie cookieToDelete = new Cookie(cookieName, null);
//       cookieToDelete.setMaxAge(0);
//       response.addCookie(cookieToDelete);
//     }
//     response.setHeader("Access-Control-Allow-Origin", "*");
//   }
// }
