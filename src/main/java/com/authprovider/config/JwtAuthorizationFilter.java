package com.authprovider.config;

import com.authprovider.exceptions.Unauthorized;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.jose4j.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final Map<String, Boolean> whitelistedRoutes = new HashMap<>(
    Map.of(
      "/api/user/login",
      true,
      "/api/user/register",
      true,
      "/api/user/roles",
      true
    )
  );

  @Autowired
  private JWTService jwtService;

  @Autowired
  private UserService userService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request)
    throws ServletException {
    String path = request.getRequestURI();
    return whitelistedRoutes.containsKey(path);
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    Cookie cookie = WebUtils.getCookie(request, "access_token");

    if (cookie == null) {
      filterChain.doFilter(request, response);
      return;
    }

    JwtClaims claims = jwtService.extractClaims(cookie.getValue());
    if (claims == null) {
      filterChain.doFilter(request, response);
      return;
    }
    String userId = jwtService.extractSubject(claims);

    // if the id is not null and the user doesn't have authToken
    if (
      userId != null &&
      SecurityContextHolder.getContext().getAuthentication() == null
    ) {
      // get user  from the db
      User userDetails = userService
        .getUserById(userId)
        .orElseThrow(() -> new UserNotFound());

      // create authToken
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
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
