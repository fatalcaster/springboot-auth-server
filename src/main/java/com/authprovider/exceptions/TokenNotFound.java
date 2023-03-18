package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "Role with that name doesn't exist"
)
public class TokenNotFound extends RuntimeException {}
