package com.authprovider.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
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

  private long expiresAt = 0;

  public long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(long expiresAt) {
    this.expiresAt = expiresAt;
  }

  public boolean isNonExpired() {
    Clock clock = Clock.systemUTC();
    long unixTime = Instant.now(clock).getEpochSecond();
    System.out.println(expiresAt + " " + unixTime);
    return expiresAt > unixTime;
  }

  private boolean isNonRevoked = true;

  public boolean getIsNonRevoked() {
    return isNonRevoked;
  }

  public void setIsNonRevoked(boolean isNonRevoked) {
    this.isNonRevoked = isNonRevoked;
  }
}
