package com.authprovider.service;

import com.authprovider.dto.ClientDTO;
import com.authprovider.model.Client;
import com.authprovider.model.Token;
import com.authprovider.model.User;
import com.authprovider.repo.TokenRepo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {

  @Autowired
  ClientService clientService;

  @Autowired
  TokenRepo tokenRepo;

  @Override
  public Optional<Token> getToken(String token) {
    return tokenRepo.findById(UUID.fromString(token));
  }

  @Override
  public void saveToken(Token token) {
    tokenRepo.save(token);
  }

  @Override
  public boolean isTokenValid(Token token) {
    if (token == null || !token.getIsNonExpired() || !token.getIsNonRevoked()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isClientTokenProvider(
    String clientId,
    String clientSecret,
    Token token
  ) {
    Client owner = token.getTokenProvider();

    if (!owner.getId().toString().equals(clientId)) {
      return false;
    }

    // if the provided secret doesn't match the existing one
    if (!clientService.secretsMatch(clientSecret, owner.getSecret())) {
      return false;
    }
    return true;
  }

  @Override
  public void setExpiredAndSave(Token token) {
    token.setIsNonExpired(false);
    saveToken(token);
  }
}
