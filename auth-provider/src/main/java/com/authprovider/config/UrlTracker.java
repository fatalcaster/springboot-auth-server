package com.authprovider.config;

public class UrlTracker {

  private UrlTracker() {}

  public static final String protocol = "http://";
  public static final String domain = protocol + "localhost:3000";
  public static final String loginPage = "http://localhost:3000/login";
  public static final String issuer = domain + "/";
  // public static final String thisBaseUrl = "http://192.168.0.12:5000";
  public static final String frontEndBaseURL = "http://192.168.0.12:3000";
}
