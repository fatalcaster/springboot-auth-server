package com.authprovider.dto.internal;

import com.authprovider.config.KeyManager;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

public class Keystore {

  public Keystore(String kid, String privateKey, String publicKey) {
    setKid(kid);
    setPrivateKey(privateKey);
    setPublicKey(publicKey);
  }

  private String kid;

  public String getKid() {
    return kid;
  }

  public void setKid(String kid) {
    this.kid = kid;
  }

  private PrivateKey privateKey;

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = KeyManager.toPrivateKey(privateKey);
  }

  private RSAPublicKey publicKey;

  public RSAPublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = KeyManager.toRSAPublic(publicKey);
  }
}
