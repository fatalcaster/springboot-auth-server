package com.authprovider.dto;

import com.authprovider.model.Role;
import jakarta.validation.constraints.NotBlank;

public class RoleRequestDTO {

  @NotBlank
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name.toUpperCase();
  }

  public Role toRole() {
    return Role.build(name);
  }
}
