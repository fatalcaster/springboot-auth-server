package com.authprovider.service;

import com.authprovider.model.Role;
import com.authprovider.service.exceptions.ConflictException;
import java.util.List;
import java.util.Optional;

public interface IRoleService {
  List<Role> getAllRoles();

  void saveRole(Role role) throws ConflictException;

  public Optional<Role> getRoleByName(String roleName);
}
