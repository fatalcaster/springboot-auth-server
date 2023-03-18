package com.authprovider.dto;

import com.authprovider.config.UrlTracker;
import com.authprovider.exceptions.InternalError;
import com.authprovider.service.jwt.IJwtDto;
import com.authprovider.service.jwt.JwtPayload;
import com.authprovider.service.jwt.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.jose4j.jwt.MalformedClaimException;
import org.springframework.security.core.GrantedAuthority;

public class UserDTO implements IJwtDto {

  private String id;

  private String email;

  private List<GrantedAuthority> roles;

  public UserDTO(String id, String email, List<? extends Object> roles) {
    this.id = id;
    this.email = email;
    assignRoles(roles);
  }

  private void assignRoles(List<? extends Object> roles) {
    this.roles = new LinkedList<>();
    roles.forEach(role ->
      this.roles.add(
          new GrantedAuthority() {
            private String authority = role.toString();

            @Override
            public String getAuthority() {
              return this.authority;
            }

            @Override
            public String toString() {
              return this.authority;
            }
          }
        )
    );
  }

  public UserDTO(UUID id, String email, List<? extends Object> roles) {
    this.id = id.toString();
    this.email = email;
    this.roles = new LinkedList<>();
    assignRoles(roles);
  }

  public UserDTO(UUID id, String email) {
    this.id = id.toString();
    this.email = email;
  }

  public UserDTO(String id, String email) {
    this.id = id;
    this.email = email;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public List<String> getRoles() {
    List<String> stringRoles = new LinkedList<>();
    this.roles.forEach(role -> stringRoles.add(role.toString()));
    return stringRoles;
  }

  @JsonIgnore
  public List<GrantedAuthority> getGrantedAuthorities() {
    return this.roles;
  }

  public UserDTO(JwtPayload payload) {
    try {
      this.fromJwtPayload(payload);
    } catch (Exception e) {
      throw new InternalError();
    }
  }

  @Override
  public void fromJwtPayload(JwtPayload payload)
    throws MalformedClaimException {
    this.id = payload.getSubject();
    this.email = payload.getSubjectEmail();
    assignRoles(payload.getStringListClaimValue("roles"));
  }

  @Override
  public JwtPayload toJwtPayload(TokenType tokenType) {
    JwtPayload payload = new JwtPayload(
      UrlTracker.issuer,
      UrlTracker.issuer,
      getId(),
      getEmail(),
      tokenType
    );
    payload.setStringListClaim("roles", getRoles());
    return payload;
  }
}
