package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "Client with that id doesn't exist"
)
public class ClientNotFound extends RuntimeException {}
