package com.authprovider.service.jwt;

import com.authprovider.config.KeyManager;
import com.authprovider.dto.internal.Keystore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Autowired
  KeyManager keyManager;

  public <T extends IJwtDto> TokenPair createTokenPair(T obj) throws Exception {
    JwtPayload payload = obj.toJwtPayload(TokenType.ACCESS_TOKEN);

    Keystore accessKeyStore = keyManager.getRandomKeys();
    String accessToken = payload.issueAndConvertToToken(
      payload,
      accessKeyStore
    );

    payload.setTokenType(TokenType.REFRESH_TOKEN);

    Keystore refreshKeyStore = keyManager.getRandomKeys();
    String refreshToken = payload.issueAndConvertToToken(
      payload,
      refreshKeyStore
    );
    return new TokenPair(accessToken, refreshToken);
  }

  public <T extends IJwtDto> TokenPair createOAuth2TokenPair(T obj)
    throws Exception {
    JwtPayload payload = obj.toJwtPayload(TokenType.ACCESS_TOKEN);

    Keystore accessKeyStore = keyManager.getRandomKeys();
    String accessToken = payload.issueAndConvertToToken(
      payload,
      accessKeyStore
    );

    payload.setTokenType(TokenType.OAUTH2_REFRESH_TOKEN);

    Keystore refreshKeyStore = keyManager.getRandomKeys();
    String refreshToken = payload.issueAndConvertToToken(
      payload,
      refreshKeyStore
    );
    return new TokenPair(accessToken, refreshToken);
  }
}
