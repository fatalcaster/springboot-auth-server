package com.authprovider.repo;

import com.authprovider.model.Client;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Transactional
public interface ClientRepo extends JpaRepository<Client, UUID> {
  @Query(
    "SELECT c FROM Client c INNER JOIN User u ON c.owner = u.id WHERE u.id = :userId"
  )
  List<Client> findByOwner(@Param("userId") UUID userId);

  @Modifying
  @Query("UPDATE Client c SET c.secret = :newSecret WHERE c.id = :clientId")
  void updateSecret(
    @Param("clientId") UUID clientId,
    @Param("newSecret") String newSecret
  );
}
