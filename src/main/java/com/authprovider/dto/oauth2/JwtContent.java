package com.authprovider.dto.oauth2;

import com.authprovider.config.UrlTracker;
import com.authprovider.service.jwt.IJwtDto;
import com.authprovider.service.jwt.JwtPayload;
import com.authprovider.service.jwt.TokenType;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.jose4j.jwt.MalformedClaimException;

public class JwtContent implements IJwtDto {

  public JwtContent(String userId, String clientId, String scope) {
    setUserId(userId);
    setClientId(clientId);
    setScope(scope);
  }

  @JsonAlias("client_id")
  private String clientId;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @JsonAlias("user_id")
  private String userId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  private String scope;

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  @Override
  public JwtPayload toJwtPayload(TokenType tokenType) {
    JwtPayload payload = new JwtPayload(
      UrlTracker.issuer,
      getClientId(),
      getUserId(),
      tokenType
    );
    payload.setClaim("scope", getScope());
    return payload;
  }

  @Override
  public void fromJwtPayload(JwtPayload payload)
    throws MalformedClaimException {
    this.userId = payload.getSubject();
    this.clientId = payload.getAudience().get(0);
    this.scope = payload.getStringClaimValue("scope");
  }
}
