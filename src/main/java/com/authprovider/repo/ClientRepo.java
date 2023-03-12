package com.authprovider.repo;

import com.authprovider.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(String name);
}
