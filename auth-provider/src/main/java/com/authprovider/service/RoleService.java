package com.authprovider.service;

import com.authprovider.model.Role;
import com.authprovider.repo.RoleRepo;
import com.authprovider.service.exceptions.ConflictException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService {

  @Autowired
  RoleRepo repo;

  @Override
  public List<Role> getAllRoles() {
    return repo.findAll();
  }

  @Override
  public void saveRole(Role role) throws ConflictException {
    repo.save(role);
  }

  @Override
  public Optional<Role> getRoleByName(String roleName) {
    return repo.findByName(roleName);
  }
}
