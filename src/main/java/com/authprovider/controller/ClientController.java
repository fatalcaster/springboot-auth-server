package com.authprovider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api")
@RestController
public class ClientController {

  @PostMapping("/client")
  public String register() {
    return "test";
  }

  @DeleteMapping("/client")
  public void delete() {}

  @GetMapping("/client")
  public void getMyClients() {}

  @GetMapping("/client/{id}")
  public void getUserClients() {}
}
