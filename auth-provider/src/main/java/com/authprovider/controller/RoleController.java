package com.authprovider.controller;

import com.authprovider.dto.RoleRequestDTO;
import com.authprovider.exceptions.RoleAlreadyExists;
import com.authprovider.model.Role;
import com.authprovider.service.RoleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(value = "/api/roles")
@RestController
public class RoleController {

  @Autowired
  RoleService roleService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public String createRole(@RequestBody RoleRequestDTO req) {
    Role role = req.toRole();

    try {
      roleService.saveRole(role);
    } catch (Exception e) {
      throw new RoleAlreadyExists();
    }

    return role.toString();
  }

  @GetMapping
  public String getRoles() {
    List<Role> roles = roleService.getAllRoles();
    return roles.toString();
  }
}
