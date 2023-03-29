package com.authprovider.dto.oauth2;

import com.authprovider.dto.SecureCookie;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

  private String accessToken;

  @JsonProperty("access_token")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  private String refreshToken;

  @JsonProperty(SecureCookie.refreshTokenKey)
  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  private String scope;

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  private Long expiresIn;

  @JsonProperty("expires_in")
  public Long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }
}
