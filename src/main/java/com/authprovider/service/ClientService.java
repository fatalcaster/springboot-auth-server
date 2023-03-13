package com.authprovider.service;

import com.authprovider.exceptions.RoleAlreadyExists;
import com.authprovider.model.Client;
import com.authprovider.repo.ClientRepo;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements IClientService {

  @Autowired
  private ClientRepo clientRepo;

  @Override
  public List<Client> getAllClients(String userId) {
    return clientRepo.findByOwner(UUID.fromString(userId));
  }

  @Override
  public void saveClient(Client client) throws RoleAlreadyExists {
    clientRepo.save(client);
  }
}
