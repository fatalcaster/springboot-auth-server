package com.authprovider.service;

import com.authprovider.dto.UserResponseDTO;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

  public static final long JWT_VALID_TIME = 5L * 60L * 60L;

  private PrivateKey privateKey;

  private String issuer = "http://codedepo.com/";

  private RSAPublicKey publicKey;

  public JWTService(
    @Value("${spring.security.jwt-private}") String privateKey,
    @Value("${spring.security.jwt-public}") String publicKey
  ) {
    this.privateKey = KeyManager.toPrivateKey(privateKey);
    this.publicKey = KeyManager.toRSAPublic(publicKey);
  }

  private JwtClaims setClaims(UserResponseDTO user) {
    JwtClaims claims = new JwtClaims();
    claims.setIssuer(issuer); // who creates the token and signs it
    claims.setAudience(issuer); // to whom the token is intended to be sent
    claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
    claims.setGeneratedJwtId(); // a unique identifier for the token
    claims.setIssuedAtToNow(); // when the token was issued/created (now)
    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
    claims.setSubject(user.getId().toString()); // the subject/principal is whom the token is about
    claims.setClaim("email", user.getEmail()); // additional claims/attributes about the subject can be added
    claims.setStringListClaim("roles", user.getRoles()); // multi-valued claims work too and will end up as a JSON array
    return claims;
  }

  public JwtClaims extractClaims(String token) {
    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
      .setExpectedAudience(issuer)
      .setRequireSubject()
      .setRequireExpirationTime() // the JWT must have an expiration time
      .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
      .setExpectedIssuer(this.issuer) // whom the JWT needs to have been issued by
      .setVerificationKey(this.publicKey) // verify the signature with the public key
      .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
        ConstraintType.PERMIT,
        AlgorithmIdentifiers.RSA_USING_SHA256
      ) // which is only RS256 here
      .build(); // create the JwtConsumer instance

    try {
      //  Validate the JWT and process it to the Claims
      return jwtConsumer.processToClaims(token);
    } catch (InvalidJwtException e) {
      // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
      // Hopefully with meaningful explanations(s) about what went wrong.
      System.err.println("Invalid JWT! " + e);

      // Programmatic access to (some) specific reasons for JWT invalidity is also possible
      // should you want different error handling behavior for certain conditions.

      // Whether or not the JWT has expired being one common reason for invalidity
      if (e.hasExpired()) {
        System.err.println("JWT expired");
      }
      return null;
    }
  }

  public String extractSubject(JwtClaims claims) {
    try {
      return claims.getSubject();
    } catch (MalformedClaimException e) {
      return null;
    }
  }

  public String generateToken(UserResponseDTO user) {
    // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
    RsaJsonWebKey rsaJsonWebKey = new RsaJsonWebKey(this.publicKey);

    // Give the JWK a Key ID (kid)
    rsaJsonWebKey.setKeyId("k1");
    rsaJsonWebKey.setPrivateKey(this.privateKey);
    // Create the Claims, which will be the content of the JWT

    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
    // In this example it is a JWS so we create a JsonWebSignature object.
    JsonWebSignature jws = new JsonWebSignature();

    JwtClaims claims = setClaims(user);
    // The payload of the JWS is JSON content of the JWT Claims
    jws.setPayload(claims.toJson());

    // The JWT is signed using the private key
    jws.setKey(rsaJsonWebKey.getPrivateKey());

    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    try {
      String jwt = jws.getCompactSerialization();
      return jwt;
    } catch (JoseException e) {
      System.err.println(e);
      throw new InternalError("Internal error - JWT");
    }
  }
}
