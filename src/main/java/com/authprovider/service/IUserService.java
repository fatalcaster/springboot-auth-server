package com.authprovider.service;

import com.authprovider.model.Client;
import com.authprovider.model.User;
import com.authprovider.service.exceptions.ConfigurationException;
import com.authprovider.service.exceptions.ConflictException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
  void saveUser(User user) throws ConflictException, ConfigurationException;

  public Optional<User> getUserByEmail(String email);

  public Optional<User> getUserById(String id);

  public List<User> getAllUsers(int page);

  public void addRole(String id, String roleName);

  public void addRole(User user, String roleName);

  public void addClient(User user, Client client);
}
