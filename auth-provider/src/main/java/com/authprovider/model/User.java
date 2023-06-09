package com.authprovider.model;

import com.authprovider.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user_entity", schema = "public")
public class User extends DateEntity implements UserDetails {

  @Id
  @UuidGenerator
  @Column(name = "id", unique = true)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "roles",
    joinColumns = @JoinColumn(name = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private List<Role> roles = new ArrayList<>();

  @JsonIgnore
  @OneToMany(
    fetch = FetchType.LAZY,
    mappedBy = "owner",
    cascade = CascadeType.REMOVE
  )
  private List<Client> clients = new ArrayList<>();

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "tokenSubject")
  private List<Token> tokens = new ArrayList<>();

  public List<Client> getClients() {
    return clients;
  }

  @JsonIgnore
  public List<Role> getRoles() {
    return roles;
  }

  @JsonProperty("roles")
  public List<String> getStrRoles() {
    List<String> rolesStr = new LinkedList<>();
    this.roles.forEach(role -> rolesStr.add(role.toString()));
    return rolesStr;
  }

  public User() {}

  public User(String email, String password) {
    setEmail(email);
    setPassword(password);
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public UserDTO toResponseDTO() {
    return new UserDTO(this.id, this.email, this.getRoles());
  }

  public String getId() {
    return this.id.toString();
  }

  @JsonProperty("email")
  @Override
  public String getUsername() {
    return this.email;
  }
}
