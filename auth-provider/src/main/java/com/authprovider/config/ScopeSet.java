package com.authprovider.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScopeSet {

  private static final Set<String> scopes = new HashSet<>(
    List.of("user:email")
  );

  private ScopeSet() {}

  /**
   * @param scopeString a string containting space separated scopes
   * @return whether the existing set contains all the scopes in the string
   */
  public static boolean containsScopeString(String scopeString) {
    String[] s = scopeString.split(" ");
    for (int i = 0; i < s.length; i++) {
      if (!scopes.contains(s[i])) return false;
    }
    return true;
  }
}
