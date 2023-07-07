package com.authprovider.controller;

import com.authprovider.config.AuthUtil;
import com.authprovider.dto.ClientDTO;
import com.authprovider.dto.UserDTO;
import com.authprovider.exceptions.ClientNotFound;
import com.authprovider.exceptions.Forbidden;
import com.authprovider.exceptions.InternalError;
import com.authprovider.exceptions.NotAuthorized;
import com.authprovider.exceptions.UserNotFound;
import com.authprovider.model.Client;
import com.authprovider.model.User;
import com.authprovider.service.ClientService;
import com.authprovider.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@CrossOrigin(origins = "accounts.codedepo.com", allowCredentials = "true")
@RequestMapping(value = "/api")
@RestController
public class ClientController {

  @Autowired
  ClientService clientService;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  AuthUtil authUtil;

  @PreAuthorize("hasRole('ROLE_USER')")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/client")
  public ClientDTO register(
    @Valid @RequestBody ClientRegistrationRequest clientRequest
  ) {
    UserDTO userDto = authUtil.getAuthorizedUser();
    if (userDto == null) {
      throw new InternalError();
    }
    User user = userService
      .getUserById(userDto.getId())
      .orElseThrow(UserNotFound::new);

    Client client = new Client();
    client.setName(clientRequest.getName());
    client.setOwner(user);
    client = clientService.encodeSecretAndSaveClient(client);

    return client.toClientDto(true);
  }

  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @DeleteMapping("/client/{clientId}")
  public void delete(@PathVariable(required = true) String clientId) {
    clientService.deleteClient(clientId);
  }

  @PutMapping("/client/{clientId}/secret")
  public ClientDTO updateSecret(
    @PathVariable(required = true) String clientId
  ) {
    Client client = clientService
      .getClientById(clientId)
      .orElseThrow(ClientNotFound::new);

    System.out.println(
      client.getOwner().getId() + " " + authUtil.getAuthorizedUser().getId()
    );
    if (
      !client.getOwner().getId().equals(authUtil.getAuthorizedUser().getId())
    ) {
      throw new NotAuthorized();
    }

    Client clientWithNewSecret = clientService.updateSecretAndSave(client);
    return clientWithNewSecret.toClientDto(true);
  }

  @GetMapping("/client")
  public List<ClientDTO> getMyClients() {
    UserDTO userDto = authUtil.getAuthorizedUser();
    if (userDto == null) {
      throw new com.authprovider.exceptions.InternalError();
    }

    List<Client> clients = clientService.getAllClients(userDto.getId());

    List<ClientDTO> clientRes = new LinkedList<>();
    clients.forEach(client -> clientRes.add(client.toClientDto()));
    return clientRes;
  }

  // @PostAuthorize("#id == authentication.principial.id or hasRole('ROLE_ADMIN')")
  @GetMapping("/client/{id}")
  public ClientDTO getUserClients(@PathVariable(name = "id") String clientId) {
    Client client = clientService
      .getClientById(clientId)
      .orElseThrow(ClientNotFound::new);

    UserDTO user = authUtil.getAuthorizedUser();

    // if (
    //   !client.getOwner().getId().equals(user.getId()) &&
    //   !user.getRoles().contains("ROLE_ADMIN")
    // ) {
    //   throw new NotAuthorized();
    // }
    return client.toClientDto();
  }
}

class ClientRegistrationRequest {

  @NotBlank
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
