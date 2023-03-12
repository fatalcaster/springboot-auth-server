package com.authprovider.controller;

import com.authprovider.dto.OpenIdAuthorizationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api")
@RestController
public class OpenIDController {

  @GetMapping(
    value = "/authorize",
    consumes = "application/x-www-form-urlencoded"
  )
  public OpenIdAuthorizationRequestDTO authorize(
    @Valid OpenIdAuthorizationRequestDTO req
  ) {
    return req;
  }
}
