package com.authprovider.service;

import com.authprovider.exceptions.UserAlreadyExists;
import com.authprovider.model.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
  void saveUser(User user) throws UserAlreadyExists;

  public Optional<User> getUserByEmail(String email);

  public Optional<User> getUserById(String id);

  public void addRole(String email, String roleName);
}
