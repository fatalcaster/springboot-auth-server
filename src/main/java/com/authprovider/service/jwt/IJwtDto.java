package com.authprovider.service.jwt;

import org.jose4j.jwt.MalformedClaimException;

public interface IJwtDto {
  public JwtPayload toJwtPayload(TokenType tokenType);

  public void fromJwtPayload(JwtPayload payload) throws MalformedClaimException;
}
