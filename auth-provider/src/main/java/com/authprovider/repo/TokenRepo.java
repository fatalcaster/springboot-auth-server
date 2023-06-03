package com.authprovider.repo;

import com.authprovider.model.Token;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<Token, UUID> {}
