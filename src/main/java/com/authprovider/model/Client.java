package com.authprovider.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "client", schema = "public")
public class Client {

  @Id
  @UuidGenerator
  @Column(name = "role_id")
  private UUID id;

  @Column
  private String secret;

  public UUID getId() {
    return id;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
