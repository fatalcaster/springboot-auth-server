package com.authprovider.config;

import com.authprovider.dto.internal.Keystore;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TODO: Implement hashmap with keyids
@Configuration
@ConfigurationProperties(prefix = "security")
public class KeyManager {

  private static final Random RANDOM = new SecureRandom();

  // private static Logger logger = LoggerFactory.getLogger(KeyManager.class);

  private List<Keystore> jwtKeyList = new LinkedList<>();

  public void setJwtKeySet(List<Map<String, Object>> jwtKeys) {
    jwtKeys.forEach(element -> {
      String id = element.get("key-id").toString();
      String pubKey = element.get("public-key").toString();
      String privKey = element.get("private-key").toString();
      jwtKeyList.add(new Keystore(id, privKey, pubKey));
    });
  }

  public Optional<Keystore> getJwtKeys(String keyId) {
    for (Keystore curr : jwtKeyList) {
      if (curr.getKid().equals(keyId)) return Optional.of(curr);
    }
    return Optional.empty();
  }

  public Keystore getRandomKeys() {
    return jwtKeyList.get(RANDOM.nextInt(jwtKeyList.size()));
  }

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
      throw new Error(e);
    }
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
      throw new Error(e);
    }
  }
}
