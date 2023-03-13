package com.authprovider.dto;

public class ClientDTO {

  private String id;

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

  public ClientDTO(String clientId, String ownerId) {
    this.id = clientId;
    this.owner = ownerId;
  }
}
