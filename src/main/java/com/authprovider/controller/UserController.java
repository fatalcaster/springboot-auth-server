package com.authprovider.controller;

import com.authprovider.model.User;
import com.authprovider.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/user")
@RestController
public class UserController {

  @Autowired
  private UserService service;

  @GetMapping("/all")
  public List<User> getUsers() {
    return service.getAllUsers();
  }
}
