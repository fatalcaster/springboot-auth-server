package com.authprovider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class ClientDTO {

  @JsonProperty("client_id")
  private String id;

  @JsonProperty("non_expired")
  private boolean isNonExpired;

  public boolean getIsNonExpired() {
    return isNonExpired;
  }

  public void setIsNonExpired(boolean isNonExpired) {
    this.isNonExpired = isNonExpired;
  }

  @JsonProperty("client_secret")
  private Optional<String> secret;

  public Optional<String> getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = Optional.of(secret);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String owner;

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public ClientDTO(String clientId, String ownerId, boolean isNonExpired) {
    this.id = clientId;
    this.owner = ownerId;
    this.isNonExpired = isNonExpired;
  }

  public ClientDTO(
    String clientId,
    String ownerId,
    String secret,
    boolean isExpired
  ) {
    this.id = clientId;
    this.owner = ownerId;
    this.secret = Optional.of(secret);
    this.isNonExpired = isExpired;
  }
}
