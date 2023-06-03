package com.authprovider.service.jwt;

public class TokenPair {

  public TokenPair(String accessToken, String refreshToken) {
    this.setAccessToken(accessToken);
    this.setRefreshToken(refreshToken);
  }

  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
