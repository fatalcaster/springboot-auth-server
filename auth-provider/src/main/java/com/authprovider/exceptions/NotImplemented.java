package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  code = HttpStatus.NOT_IMPLEMENTED,
  reason = "Not implemented yet"
)
public class NotImplemented extends RuntimeException {}
