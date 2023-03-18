package com.authprovider.controller;

import com.authprovider.model.Token;
import com.authprovider.service.TokenService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/test")
@RestController
public class TestController {

  @Autowired
  TokenService tokenService;

  @GetMapping
  public Optional<Token> getSomething(String tokenId) {
    return tokenService.getToken(tokenId);
  }
}
