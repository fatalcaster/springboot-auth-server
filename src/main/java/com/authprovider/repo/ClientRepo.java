package com.authprovider.repo;

import com.authprovider.model.Client;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, UUID> {
  List<Client> findByOwner(UUID userId);
}
