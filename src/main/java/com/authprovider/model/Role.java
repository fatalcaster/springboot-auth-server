package com.authprovider.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "role", schema = "public")
public class Role implements GrantedAuthority {

  @Id
  @UuidGenerator
  @JsonIgnore
  @Column(name = "role_id")
  private UUID id;

  @Column(name = "name", unique = true)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private List<User> users = new ArrayList<>();

  public void setRole(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static Role build(String name) {
    Role role = new Role();
    role.setRole(name);
    return role;
  }

  @Override
  public String getAuthority() {
    return this.name;
  }
}
