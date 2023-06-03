package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  value = HttpStatus.CONFLICT,
  reason = "That role already exists!"
)
public class RoleAlreadyExists extends RuntimeException {}
