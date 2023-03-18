package com.authprovider.dto.oauth2;

import com.authprovider.config.ScopeSet;
import com.authprovider.exceptions.BadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

public class AuthorizationDTO {

  @Autowired
  @JsonIgnore
  @Transient
  private ScopeSet availableScopes;

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

  private String scope;

  public void setScope(String scopeString) {
    if (!availableScopes.containsScopeString(scopeString)) {
      throw new BadRequest("Invalid scope");
    }
    scope += scopeString + " ";
  }

  public String getScope() {
    return this.scope;
  }
}
