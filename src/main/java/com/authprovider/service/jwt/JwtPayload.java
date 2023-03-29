package com.authprovider.service.jwt;

import com.authprovider.config.UrlTracker;
import com.authprovider.dto.internal.Keystore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.security.Key;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;

public class JwtPayload extends JwtClaims {

  private TokenType tokenType;

  private List<String> scope;

  public List<String> getScope() {
    return scope;
  }

  @JsonIgnore
  public TokenType getTokenType() {
    return tokenType;
  }

  public void setTokenType(TokenType tokenType) {
    this.tokenType = tokenType;
  }

  public JwtPayload(
    String issuer,
    String audience,
    String subject,
    List<String> scope,
    TokenType tokenType
  ) {
    this.setIssuer(issuer);
    this.setAudience(audience);
    this.setSubject(subject);
    this.setTokenType(tokenType);
    this.scope = scope;
    this.setExpirationTimeMinutesInTheFuture(0); // security measure
  }

  public void issue() {
    if (this.tokenType == TokenType.REFRESH_TOKEN) {
      this.setExpirationTimeMinutesInTheFuture(60 * 24 * 60F);
    } else if (this.tokenType == TokenType.OAUTH2_REFRESH_TOKEN) {
      this.setExpirationTimeMinutesInTheFuture(60 * 24 * 7F);
    } else {
      this.setExpirationTimeMinutesInTheFuture(10);
    }
    this.setNotBeforeMinutesInThePast(2);
    this.setIssuedAtToNow();
  }

  public static Optional<JwtPayload> buildFromToken(
    String token,
    RSAPublicKey publicKey,
    String audience
  ) {
    if (publicKey == null) return Optional.empty();

    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
      .setRequireSubject()
      .setRequireExpirationTime()
      .setExpectedAudience(audience)
      .setAllowedClockSkewInSeconds(30)
      .setExpectedIssuer(UrlTracker.issuer)
      .setVerificationKey(publicKey)
      .setJwsAlgorithmConstraints(
        ConstraintType.PERMIT,
        AlgorithmIdentifiers.RSA_USING_SHA256
      ) // which is only RS256 here
      .build(); // create the JwtConsumer instance

    try {
      JwtClaims claims = jwtConsumer.processToClaims(token);
      JwtPayload payload = new JwtPayload(
        claims.getIssuer(),
        claims.getAudience().get(0),
        claims.getSubject(),
        claims.getStringListClaimValue("scope"),
        TokenType.ACCESS_TOKEN
      );
      //  Validate the JWT and process it to the Claims
      return Optional.of(payload);
    } catch (Exception e) {
      // throw new RuntimeException(e);
      System.err.println(e);
      return Optional.empty();
    }
  }

  public static String extractKid(String token) {
    try {
      return JsonWebStructure
        .fromCompactSerialization(token)
        .getKeyIdHeaderValue();
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }

  public String issueAndConvertToToken(JwtPayload payload, Keystore keystore)
    throws Exception {
    RsaJsonWebKey rsaJsonWebKey = new RsaJsonWebKey(keystore.getPublicKey());

    // Give the JWK a Key ID (kid)
    rsaJsonWebKey.setKeyId(keystore.getKid());
    rsaJsonWebKey.setPrivateKey(keystore.getPrivateKey());
    // Create the Claims, which will be the content of the JWT

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    JsonWebSignature jws = new JsonWebSignature();

    setStringListClaim("scope", scope);
    payload.issue();
    jws.setPayload(payload.toJson());

    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey());

    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    try {
      return jws.getCompactSerialization();
    } catch (JoseException e) {
      throw new Exception("Something went wrong - jwt");
    }
  }
}
