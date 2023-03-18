package com.authprovider.service;

import com.authprovider.model.Token;
import java.util.Optional;

public interface ITokenService {
  Optional<Token> getToken(String token);
  void saveToken(Token token);
  boolean isTokenValid(Token token);
  boolean isClientTokenProvider(
    String clientId,
    String clientSecret,
    Token token
  );
  void setExpiredAndSave(Token token);
}
