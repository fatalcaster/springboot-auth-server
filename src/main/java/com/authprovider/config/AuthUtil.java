package com.authprovider.config;

import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserDTO;
import com.authprovider.dto.internal.Keystore;
import com.authprovider.service.jwt.JwtPayload;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class AuthUtil {

  @Autowired
  KeyManager keyManager;

  public UserDTO extractUser(HttpServletRequest request) {
    // Cookie cookie = WebUtils.getCookie(request, SecureCookie.accessTokenKey);
    System.out.println(request.getHeaderNames());
    String token = request.getHeader(SecureCookie.AuthorizationHeader);
    if (token == null) {
      return null;
    }

    Keystore keystore = parseKeystore(token).orElse(null);

    if (keystore == null) return null;

    JwtPayload jwtPayload = JwtPayload
      .buildFromToken(token, keystore.getPublicKey(), UrlTracker.issuer)
      .orElse(null);
    if (jwtPayload == null) return null;

    return new UserDTO(jwtPayload);
  }

  public Optional<Keystore> parseKeystore(String token) {
    if (token == null) return Optional.empty();

    String kid = JwtPayload.extractKid(token);

    Keystore keystore = keyManager.getJwtKeys(kid).orElse(null);

    if (keystore == null) return Optional.empty();
    return Optional.of(keystore);
  }

  public UserDTO getAuthorizedUser() {
    return (UserDTO) SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();
  }
}
