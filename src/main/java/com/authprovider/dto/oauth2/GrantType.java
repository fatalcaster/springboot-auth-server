package com.authprovider.dto.oauth2;

public enum GrantType {
  REFRESH_TOKEN("refresh_token"),
  AUTHORIZATION_CODE("authorization_code");

  private String type;

  GrantType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return this.type;
  }
}
