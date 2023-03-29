package com.authprovider.config;

import com.authprovider.dto.UserDTO;
import com.authprovider.exceptions.NotAuthorized;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final Set<String> whitelistedRoutes = new HashSet<>(
    List.of(
      "/api/auth/login",
      "/api/auth/register",
      "/api/auth/refresh",
      "/api/auth/logout",
      "/api/oauth2/token"
    )
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
    UserDTO user = authUtil.extractUser(request);

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
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
