package com.authprovider.controller;

import com.authprovider.dto.ClientDTO;
import com.authprovider.model.Client;
import com.authprovider.service.ClientService;
import java.net.http.HttpRequest;
import java.security.Principal;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RequestMapping(value = "/api")
@RestController
@PreAuthorize("hasRole('user')")
public class ClientController {

  @Autowired
  ClientService clientService;

  @PostMapping("/client")
  public String register(HttpRequest request) {
    return "test";
  }

  @DeleteMapping("/client")
  public void delete() {}

  @GetMapping("/client")
  public void getMyClients() {}

  @GetMapping("/client/{id}")
  public void getUserClients() {}
}
