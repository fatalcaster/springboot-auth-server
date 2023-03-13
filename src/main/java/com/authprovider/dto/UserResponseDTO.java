package com.authprovider.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public class UserResponseDTO {

  private UUID id;

  private String email;

  private List<GrantedAuthority> roles;

  public UserResponseDTO(UUID id, String email, List<? extends Object> roles) {
    this.id = id;
    this.email = email;
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

  public UserResponseDTO(UUID id, String email) {
    this.id = id;
    this.email = email;
  }

  public String getId() {
    return id.toString();
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
}
