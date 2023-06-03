package com.authprovider.dto;

import com.authprovider.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSerializer extends StdSerializer<User> {

  public UserSerializer() {
    this(null);
  }

  public UserSerializer(Class<User> t) {
    super(t);
  }

  @Override
  public void serialize(
    User user,
    JsonGenerator jgen,
    SerializerProvider provider
  ) throws IOException {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    jgen.writeStartObject();

    String authoritiesStr = authentication.getAuthorities().toString();

    if (authoritiesStr.contains("ADMIN")) {
      // jgen.writeArrayFieldStart("roles");
      // jgen.writeArray(
      //   user.getAuthorities().stream().toArray(String[]::new),
      //   0,
      //   user.getAuthorities().size()
      // );
      // jgen.writeEndArray();
      jgen.writeStringField("email", user.getUsername());
    }
    jgen.writeEndObject();
  }
}
