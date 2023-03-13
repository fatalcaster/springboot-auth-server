package com.authprovider.config;

import com.authprovider.dto.UserResponseDTO;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.User;
import com.authprovider.service.JWTService;
import com.authprovider.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jose4j.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final Set<String> whitelistedRoutes = new HashSet<>(
    Set.of("/api/auth/login", "/api/auth/register", "/api/roles")
  );

  @Autowired
  AuthUtil authUtil;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request)
    throws ServletException {
    String path = request.getRequestURI();
    return whitelistedRoutes.contains(path);
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    if (shouldNotFilter(request)) {
      filterChain.doFilter(request, response);
      return;
    }
    UserResponseDTO user = authUtil.extractUser(request);

    // if there's no valid jwt
    if (
      user != null &&
      SecurityContextHolder.getContext().getAuthentication() == null
    ) {
      // create authToken
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        user,
        null,
        user.getGrantedAuthorities()
      );

      // get details from request
      authToken.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
      );

      // pass the token to security context
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);
  }
}
