package com.authprovider.repo;

import com.authprovider.model.Client;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepo extends JpaRepository<Client, UUID> {
  @Query(
    "SELECT c FROM Client c INNER JOIN User u ON c.owner = u.id WHERE u.id = :userId"
  )
  List<Client> findByOwner(@Param("userId") UUID userId);
}
