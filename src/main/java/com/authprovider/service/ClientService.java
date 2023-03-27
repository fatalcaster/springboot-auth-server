package com.authprovider.service;

import com.authprovider.model.Client;
import com.authprovider.repo.ClientRepo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements IClientService {

  @Autowired
  private ClientRepo clientRepo;

  @Autowired
  private PasswordEncoder secretEncoder;

  @Override
  public List<Client> getAllClients(String userId) {
    return clientRepo.findByOwner(UUID.fromString(userId));
  }

  public List<Client> getAllClients() {
    return clientRepo.findAll();
  }

  @Override
  public Client saveClient(Client client) {
    String secret = client.getSecret();
    client.setSecret(secretEncoder.encode(secret));
    clientRepo.save(client);
    client.setSecret(secret);
    return client;
  }

  @Override
  public Optional<Client> getClientById(String clientId) {
    return clientRepo.findById(UUID.fromString(clientId));
  }

  @Override
  public boolean secretsMatch(String secret, String hashedSecret) {
    return secretEncoder.matches(secret, hashedSecret);
  }

  @Override
  public void deleteClient(String clientId) {
    clientRepo.deleteById(UUID.fromString(clientId));
  }
}
