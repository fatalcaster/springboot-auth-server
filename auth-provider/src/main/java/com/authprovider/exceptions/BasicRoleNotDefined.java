package com.authprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
  code = HttpStatus.INTERNAL_SERVER_ERROR,
  reason = "Role user undefined"
)
public class BasicRoleNotDefined extends RuntimeException {

  public BasicRoleNotDefined() {
    super("Basic role user was not defined!");
  }
}
