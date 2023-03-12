package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  value = HttpStatus.NOT_FOUND,
  reason = "User with that email doesn't exist"
)
public class UserNotFound extends RuntimeException {}
