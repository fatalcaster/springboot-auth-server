package com.authprovider.config;

import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserDTO;
import com.authprovider.dto.internal.Keystore;
import com.authprovider.service.jwt.JwtPayload;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class AuthUtil {

  @Autowired
  KeyManager keyManager;

  public UserDTO extractUser(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, SecureCookie.accessTokenKey);
    if (cookie == null) return null;
    String kid = JwtPayload.extractKid(cookie.getValue());

    Keystore keystore = keyManager.getJwtKeys(kid).orElse(null);

    if (keystore == null) return null;
    JwtPayload jwtPayload = JwtPayload
      .buildFromToken(
        cookie.getValue(),
        keystore.getPublicKey(),
        UrlTracker.issuer
      )
      .orElse(null);
    if (jwtPayload == null) return null;

    return new UserDTO(jwtPayload);
  }
}
