package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  value = HttpStatus.INTERNAL_SERVER_ERROR,
  reason = "Something went wrong"
)
public class InternalError extends RuntimeException {}
