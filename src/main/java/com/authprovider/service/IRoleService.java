package com.authprovider.service;

import com.authprovider.exceptions.RoleAlreadyExists;
import com.authprovider.model.Role;
import java.util.List;
import java.util.Optional;

public interface IRoleService {
  List<Role> getAllRoles();

  void saveRole(Role role) throws RoleAlreadyExists;

  public Optional<Role> getRoleByName(String roleName);
}
