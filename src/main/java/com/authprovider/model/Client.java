package com.authprovider.model;

import com.authprovider.dto.ClientDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "client", schema = "public")
public class Client {

  @Id
  @UuidGenerator
  @Column(name = "id", unique = true)
  @JsonProperty("client_id")
  private UUID id;

  @Column
  @JsonProperty("client_secret")
  private String secret = UUID.randomUUID().toString();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User owner;

  @OneToMany(
    mappedBy = "tokenProvider",
    fetch = FetchType.LAZY,
    cascade = CascadeType.REMOVE
  )
  private List<Token> tokenLists;

  // @ManyToMany(fetch = FetchType.LAZY)
  // @JoinColumn(name = "user_id")
  // private List<User> clientList;

  public User getOwner() {
    return owner;
  }

  private boolean isNonExpired = true;

  public boolean getIsNonExpired() {
    return isNonExpired;
  }

  public void setIsNonExpired(boolean isNonExpired) {
    this.isNonExpired = isNonExpired;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public UUID getId() {
    return id;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public ClientDTO toClientDto() {
    return new ClientDTO(
      this.id.toString(),
      this.owner.getId(),
      this.getIsNonExpired()
    );
  }

  public ClientDTO toClientDto(boolean shareSecret) {
    if (shareSecret) {
      return new ClientDTO(
        this.id.toString(),
        this.owner.getId(),
        this.getSecret(),
        this.getIsNonExpired()
      );
    }
    throw new InternalError(
      "You must explicitly share the secret -> shareSecret = true"
    );
  }
}
