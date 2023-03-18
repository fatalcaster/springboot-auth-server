package com.authprovider.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity
@Table(name = "token", schema = "public")
public class Token {

  @Id
  @UuidGenerator
  @JsonIgnore
  @Column(name = "id", unique = true)
  private UUID id;

  public UUID getId() {
    return id;
  }

  private String scope;

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "token_provider", nullable = false)
  private Client tokenProvider;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "token_subject", nullable = false)
  private User tokenSubject;

  public User getTokenSubject() {
    return tokenSubject;
  }

  public void setTokenSubject(User tokenSubject) {
    this.tokenSubject = tokenSubject;
  }

  public Client getTokenProvider() {
    return tokenProvider;
  }

  public void setTokenProvider(Client tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  private boolean isNonExpired = true;

  public boolean getIsNonExpired() {
    return isNonExpired;
  }

  public void setIsNonExpired(boolean isNonExpired) {
    this.isNonExpired = isNonExpired;
  }

  private boolean isNonRevoked = true;

  public boolean getIsNonRevoked() {
    return isNonRevoked;
  }

  public void setIsNonRevoked(boolean isNonRevoked) {
    this.isNonRevoked = isNonRevoked;
  }
}
