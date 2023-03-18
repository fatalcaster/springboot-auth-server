package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.dto.ClientDTO;
import com.authprovider.dto.UserDTO;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.Client;
import com.authprovider.model.User;
import com.authprovider.service.ClientService;
import com.authprovider.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api")
@RestController
@PreAuthorize("hasRole('user')")
public class ClientController {

  @Autowired
  ClientService clientService;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  AuthUtil authUtil;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/client")
  public ClientDTO register(HttpServletRequest request) {
    UserDTO userDto = authUtil.extractUser(request);
    if (userDto == null) {
      throw new InternalError();
    }
    User user = userService
      .getUserById(userDto.getId())
      .orElseThrow(UserNotFound::new);

    Client client = new Client();
    client.setOwner(user);
    client = clientService.saveClient(client);

    return client.toClientDto(true);
  }

  @DeleteMapping("/client")
  public void delete() {}

  @GetMapping("/client")
  public List<ClientDTO> getMyClients(HttpServletRequest request) {
    UserDTO userDto = authUtil.extractUser(request);
    if (userDto == null) {
      throw new com.authprovider.exceptions.InternalError();
    }

    List<Client> clients = clientService.getAllClients(userDto.getId());

    List<ClientDTO> clientRes = new LinkedList<>();
    clients.forEach(client -> clientRes.add(client.toClientDto()));
    return clientRes;
  }

  @GetMapping("/client/{id}")
  public void getUserClients() {}
}
