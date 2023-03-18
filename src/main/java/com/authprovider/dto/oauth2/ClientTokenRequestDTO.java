package com.authprovider.dto.oauth2;

import java.util.Optional;

public class ClientTokenRequestDTO {

  private String clientId;

  public String getClientId() {
    return clientId;
  }

  public void setClient_id(String clientId) {
    this.clientId = clientId;
  }

  private String clientSecret;

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClient_secret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  private String grantType;

  public String getGrantType() {
    return grantType;
  }

  public void setGrant_type(String grantType) {
    this.grantType = grantType;
  }

  private Optional<String> code;

  public Optional<String> getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = Optional.of(code);
  }

  private String redirectUri;

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirect_uri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  private Optional<String> refreshToken;

  public Optional<String> getRefreshToken() {
    return refreshToken;
  }

  public void setRefresh_token(String refreshToken) {
    this.refreshToken = Optional.of(refreshToken);
  }
}
