package com.authprovider.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDTOSerializer extends StdSerializer<UserDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(
    UserDTOSerializer.class
  );

  public UserDTOSerializer() {
    this(null);
  }

  public UserDTOSerializer(Class<UserDTO> t) {
    super(t);
  }

  @Override
  public void serialize(
    UserDTO user,
    JsonGenerator jgen,
    SerializerProvider provider
  ) throws IOException {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    jgen.writeStartObject();

    if (authentication.getAuthorities().contains("user")) {
      jgen.writeArrayFieldStart("roles");
      jgen.writeArray(
        user.getAuthorities().stream().toArray(String[]::new),
        0,
        user.getAuthorities().size()
      );
      jgen.writeEndArray();
      jgen.writeStringField("email", user.getEmail());
    } else if (authentication.getAuthorities().contains("user:email")) {
      jgen.writeStringField("email", user.getEmail());
    }
    jgen.writeStringField("id", user.getId());

    jgen.writeEndObject();
  }
}
