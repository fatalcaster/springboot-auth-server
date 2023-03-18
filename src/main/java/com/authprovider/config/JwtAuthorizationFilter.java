package com.authprovider.config;

import com.authprovider.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final Set<String> whitelistedRoutes = new HashSet<>(
    List.of(
      "/api/auth/login",
      "/api/auth/register",
      "/api/roles",
      "/api/test",
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
    } else if (user == null) {
      final String redirectURL = request.getQueryString() != null
        ? request
          .getRequestURL()
          .append('?')
          .append(request.getQueryString())
          .toString()
        : request.getRequestURL().toString();

      response.sendRedirect(UrlTracker.loginPage + "?redirect=" + redirectURL);
    }
    filterChain.doFilter(request, response);
  }
}
