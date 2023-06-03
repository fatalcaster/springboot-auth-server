package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.dto.UserDTO;
import com.authprovider.dto.oauth2.AccessTokenResponse;
import com.authprovider.dto.oauth2.AuthorizationDTO;
import com.authprovider.dto.oauth2.ClientTokenRequestDTO;
import com.authprovider.dto.oauth2.GrantType;
import com.authprovider.exceptions.BadRequest;
import com.authprovider.exceptions.ClientNotFound;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.NotAuthorized;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.Client;
import com.authprovider.model.Token;
import com.authprovider.model.User;
import com.authprovider.service.ClientService;
import com.authprovider.service.TokenService;
import com.authprovider.service.UserService;
import com.authprovider.service.jwt.JwtService;
import com.authprovider.service.jwt.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/oauth2")
@RestController
public class OAuth2Controller {

  @Autowired
  TokenService tokenService;

  @Autowired
  ClientService clientService;

  @Autowired
  JwtService jwtService;

  @Autowired
  UserService userService;

  @Autowired
  AuthUtil authUtil;

  @GetMapping(value = "/authorize")
  public String authorize(
    @Valid AuthorizationDTO data,
    HttpServletResponse response
  ) {
    Client client = clientService
      .getClientById(data.getClientId())
      .orElseThrow(ClientNotFound::new);

    User user = userService
      .getUserById(
        (
          (UserDTO) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal()
        ).getId()
      )
      .orElseThrow(UserNotFound::new);

    if (
      !user.isAccountNonExpired() ||
      !user.isAccountNonLocked() ||
      !user.isCredentialsNonExpired()
    ) {
      throw new NotAuthorized("The account is either expired or locked");
    }

    Token token = new Token();
    token.setTokenProvider(client);
    token.setScope(data.getScope());
    token.setTokenSubject(user);
    token.setExpiresAt(
      LocalDateTime.now().plusMinutes(30).toEpochSecond(ZoneOffset.UTC)
    );

    tokenService.saveToken(token);
    return data.getRedirectUri() + "?code=" + token.getId();
    // try {
    // response.setStatus(200);
    // response.sendRedirect(redirectUrl);
    // } catch (IOException e) {
    // System.out.println(e);
    // throw new InternalError();
    // }
  }

  @PostMapping(value = "/token")
  public AccessTokenResponse getToken(
    @RequestBody(required = true) ClientTokenRequestDTO clientPayload
  ) {
    if (
      clientPayload.getGrantType().equals(GrantType.REFRESH_TOKEN.toString())
    ) {
      return processRefreshToken(clientPayload);
    } else if (
      clientPayload
        .getGrantType()
        .equals(GrantType.AUTHORIZATION_CODE.toString())
    ) {
      return processAuthorizationCode(clientPayload);
    }
    throw new BadRequest();
  }

  private AccessTokenResponse processAuthorizationCode(
    ClientTokenRequestDTO clientPayload
  ) {
    String token_id = clientPayload
      .getCode()
      .orElseThrow(() -> new BadRequest("You must provide authorization code"));

    Token token = tokenService.getToken(token_id).orElse(null);

    if (!tokenService.isTokenValid(token)) {
      throw new NotAuthorized(
        "The provided token is either missing expired or revoked. " +
        token.isNonExpired() +
        token.getIsNonRevoked()
      );
    }

    if (
      !tokenService.isClientTokenProvider(
        clientPayload.getClientId(),
        clientPayload.getClientSecret(),
        token
      )
    ) {
      throw new NotAuthorized("You did not provide this token");
    }

    AccessTokenResponse response = processTokenResponse(
      token.getTokenSubject(),
      token.getTokenProvider(),
      token
    );

    tokenService.setExpiredAndSave(token);

    return response;
  }

  private AccessTokenResponse processTokenResponse(
    User user,
    Client owner,
    Token token
  ) {
    AccessTokenResponse response = new AccessTokenResponse();

    UserDTO jwt = new UserDTO(
      user.getId(),
      owner.getId().toString(),
      token.getScope()
    );

    try {
      TokenPair tokens = jwtService.createOAuth2TokenPair(jwt);

      response.setScope(token.getScope());
      response.setAccessToken(tokens.getAccessToken());
      response.setRefreshToken(tokens.getRefreshToken());
      response.setExpiresIn(604800L);

      return response;
    } catch (Exception e) {
      System.err.println(e);
      throw new InternalError();
    }
  }

  private AccessTokenResponse processRefreshToken(
    ClientTokenRequestDTO clientPayload
  ) {
    String token_id = clientPayload
      .getCode()
      .orElseThrow(() -> new BadRequest("You must provide refresh code"));

    Token token = tokenService.getToken(token_id).orElse(null);

    // checks if the token is null, revoked or expired
    if (!tokenService.isTokenValid(token)) {
      throw new NotAuthorized(
        "The provided token is either missing expired or revoked."
      );
    }

    if (
      !tokenService.isClientTokenProvider(
        clientPayload.getClientId(),
        clientPayload.getClientSecret(),
        token
      )
    ) {
      throw new NotAuthorized("You did not provide this token");
    }

    return processTokenResponse(
      token.getTokenSubject(),
      token.getTokenProvider(),
      token
    );
  }
}
