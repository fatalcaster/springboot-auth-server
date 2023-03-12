package com.authprovider.dto;

import com.authprovider.model.Role;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UserResponseDTO {

  private UUID id;

  private String email;

  private List<String> roles;

  public UserResponseDTO(UUID id, String email, List<Role> roles) {
    this.id = id;
    this.email = email;
    this.roles = new LinkedList<>();
    roles.forEach(role -> this.roles.add(role.toString()));
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public List<String> getRoles() {
    return roles;
  }
}
