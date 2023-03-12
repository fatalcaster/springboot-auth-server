package com.authprovider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OpenIdAuthorizationRequestDTO {

  @NotBlank
  @Pattern(regexp = "^(code)$")
  private String responseType;

  public String getResponseType() {
    return responseType;
  }

  public void setResponse_type(String responseType) {
    this.responseType = responseType;
  }

  @NotBlank
  private String clientId;

  public String getClientId() {
    return clientId;
  }

  public void setClient_id(String clientId) {
    this.clientId = clientId;
  }

  @NotBlank
  private String redirectUri;

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirect_uri(String redirectUri) {
    this.redirectUri = redirectUri;
  }
}
