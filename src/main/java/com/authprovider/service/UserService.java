package com.authprovider.service;

import com.authprovider.exceptions.BasicRoleNotDefined;
import com.authprovider.exceptions.RoleNotFound;
import com.authprovider.exceptions.UserAlreadyExists;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.Client;
import com.authprovider.model.Role;
import com.authprovider.model.User;
import com.authprovider.repo.RoleRepo;
import com.authprovider.repo.UserRepo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private RoleRepo roleRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public List<User> getAllUsers() {
    return userRepo.findAll();
  }

  public void saveUser(User user) {
    boolean userExist = getUserByEmail(user.getUsername()).isPresent();
    if (userExist) {
      throw new UserAlreadyExists();
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    Role userRole = roleRepo
      .findByName("user")
      .orElseThrow(BasicRoleNotDefined::new);
    user.getRoles().add(userRole);
    userRepo.save(user);
  }

  public boolean passwordsMatch(User user, String password) {
    return passwordEncoder.matches(password, user.getPassword());
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  @Override
  public void addRole(String id, String roleName) {
    Role role = roleRepo.findByName(roleName).orElseThrow(RoleNotFound::new);
    User user = userRepo
      .findById(UUID.fromString(id))
      .orElseThrow(UserNotFound::new);
    user.getRoles().add(role);
  }

  @Override
  public Optional<User> getUserById(String id) {
    return userRepo.findById(UUID.fromString(id));
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    return userRepo.findByEmail(email).orElse(null);
  }

  @Override
  public void addClient(User user, Client client) {
    user.getClients().add(client);
    this.saveUser(user);
  }

  @Override
  public void addRole(User user, String roleName) {
    Role role = roleRepo.findByName(roleName).orElseThrow(RoleNotFound::new);
    user.getRoles().add(role);
  }
}
