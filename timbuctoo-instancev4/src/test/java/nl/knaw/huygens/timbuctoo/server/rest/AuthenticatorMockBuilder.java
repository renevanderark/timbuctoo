package nl.knaw.huygens.timbuctoo.server.rest;

import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatorMockBuilder {

  private final JsonBasedAuthenticator authenticator;

  private AuthenticatorMockBuilder() throws LocalLoginUnavailableException {
    authenticator = mock(JsonBasedAuthenticator.class);
    when(authenticator.authenticate(anyString(), anyString())).thenReturn(Optional.empty());
  }

  public static AuthenticatorMockBuilder authenticator() throws LocalLoginUnavailableException {
    return new AuthenticatorMockBuilder();
  }

  public AuthenticatorMockBuilder withPidFor(String username, String password, String pid)
    throws LocalLoginUnavailableException {
    when(authenticator.authenticate(username, password)).thenReturn(Optional.of(pid));
    return this;
  }

  public JsonBasedAuthenticator build() {
    return authenticator;
  }
}
