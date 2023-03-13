package com.authprovider.model;

import com.authprovider.dto.ClientDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "client", schema = "public")
public class Client {

  @Id
  @UuidGenerator
  @Column(name = "id")
  private UUID id;

  @Column
  private String secret;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User owner;

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
    return new ClientDTO(this.id.toString(), this.owner.getId());
  }
}
