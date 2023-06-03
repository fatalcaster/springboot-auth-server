package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class Forbidden extends RuntimeException {

  public Forbidden(String message) {
    super(message);
  }

  public Forbidden() {
    super();
  }
}
