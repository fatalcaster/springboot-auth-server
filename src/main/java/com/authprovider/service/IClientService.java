package com.authprovider.service;

import com.authprovider.exceptions.RoleAlreadyExists;
import com.authprovider.model.Client;
import com.authprovider.model.Role;
import java.util.List;

public interface IClientService {
  List<Client> getAllClients(String userId);

  void saveClient(Client client) throws RoleAlreadyExists;
}
