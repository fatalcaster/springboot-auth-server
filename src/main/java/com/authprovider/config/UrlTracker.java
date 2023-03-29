package com.authprovider.config;

public class UrlTracker {

  private UrlTracker() {}

  public static final String protocol = "http://";
  public static final String domain = protocol + "localhost:3000";
  public static final String loginPage = "http://localhost:3000/login";
  public static final String issuer = domain + "/";
}
