package com.authprovider.service;

import com.authprovider.model.Client;
import java.util.List;
import java.util.Optional;

public interface IClientService {
  List<Client> getAllClients(String userId);
  List<Client> getAllClients();

  Optional<Client> getClientById(String clientId);
  Client encodeSecretAndSaveClient(Client client);
  Client updateSecretAndSave(Client client);
  Client saveClient(Client client);
  boolean secretsMatch(String secret, String hashedSecret);
  void deleteClient(String clientId);
}
