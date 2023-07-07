package com.authprovider.service;

import com.authprovider.model.Client;
import com.authprovider.model.Token;
import com.authprovider.repo.TokenRepo;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {

  private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);

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
    if (token == null || !token.isNonExpired() || !token.getIsNonRevoked()) {
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
      LOG.info("Client Ids don't match");
      return false;
    }

    // if the provided secret doesn't match the existing one
    if (!clientService.secretsMatch(clientSecret, owner.getSecret())) {
      LOG.info("Client secrets don't match");
      return false;
    }
    return true;
  }

  @Override
  public void setExpiredAndSave(Token token) {
    token.setExpiresAt(953607800); // time in the past, unix representation in seconds
    saveToken(token);
  }
}
