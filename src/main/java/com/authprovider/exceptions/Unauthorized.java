package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Not authorized")
public class Unauthorized extends RuntimeException {

  public Unauthorized() {
    super();
  }

  public Unauthorized(String message) {
    super(message);
  }
}
