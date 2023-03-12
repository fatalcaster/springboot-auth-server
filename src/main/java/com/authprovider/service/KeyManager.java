package com.authprovider.service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyManager {

  private static Logger logger = LoggerFactory.getLogger(KeyManager.class);

  private KeyManager() {}

  private static String removeHeaders(String key) {
    String clean = key.replace("---*---", "");
    clean = clean.replaceAll("\\s+", "");
    return clean;
  }

  public static PrivateKey toPrivateKey(String privateKey) {
    String pkcs8Pem = removeHeaders(privateKey);

    // Base64 decode the result
    byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);

    // extract the private key
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      if (e instanceof NoSuchAlgorithmException) logger.error(
        "INVALID SIGNING ALGORITHM - KEY MANAGER"
      ); else logger.error("INVALID KEY - KEY MANAGER");
      logger.error(e.getMessage());
      System.exit(1);
    }
    return null;
  }

  public static RSAPublicKey toRSAPublic(String publicKey) {
    String pkcs8Pem = removeHeaders(publicKey);

    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(
        Base64.getDecoder().decode(pkcs8Pem)
      );
      return (RSAPublicKey) kf.generatePublic(keySpecX509);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      if (e instanceof NoSuchAlgorithmException) logger.error(
        "Invalid signing algorithn - KEY MANAGER",
        e
      ); else logger.error("Invalid key - KeyManager", e);
      System.exit(1);
    }
    return null;
  }
}
