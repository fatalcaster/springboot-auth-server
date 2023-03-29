package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Not authorized")
public class NotAuthorized extends RuntimeException {

  public NotAuthorized() {
    super();
  }

  public NotAuthorized(String message) {
    super(message);
  }
}
