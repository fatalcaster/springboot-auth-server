package com.authprovider;

import com.authprovider.model.Role;
import com.authprovider.repo.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuthProviderApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthProviderApplication.class, args);
  }

  @Bean
  public CommandLineRunner demoData(RoleRepo repo) {
    return args -> {
      try {
        Role role = new Role();
        role.setRole("ROLE_USER");
        repo.save(role);
      } catch (Exception e) {}
    };
  }
}
