package nl.knaw.huygens.timbuctoo.security;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;

import nl.knaw.huygens.security.SecurityInformation;
import nl.knaw.huygens.timbuctoo.model.User;
import nl.knaw.huygens.timbuctoo.security.UserSecurityContextCreator;
import nl.knaw.huygens.timbuctoo.storage.StorageManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.google.common.collect.Lists;

public class UserSecurityContextCreatorTest {

  private UserSecurityContextCreator instance;
  private StorageManager storageManager;

  @Before
  public void setUp() {
    storageManager = mock(StorageManager.class);
    instance = new UserSecurityContextCreator(storageManager);
  }

  @After
  public void tearDown() {
    reset(storageManager);
  }

  @Test
  public void testCreateSecurityContextKnownUser() throws IOException {

    String applicationName = "test";
    String displayName = "displayName";
    String userId = "test123";

    User user = createUser(applicationName, displayName, userId);

    SecurityInformation securityInformation = createSecurityInformation(applicationName, displayName, userId);

    when(storageManager.searchEntity(Matchers.<Class<User>> any(), any(User.class))).thenReturn(user);

    instance.createSecurityContext(securityInformation);

    verify(storageManager, only()).searchEntity(Matchers.<Class<User>> any(), any(User.class));
    verify(storageManager, never()).addEntity(Matchers.<Class<User>> any(), any(User.class));

  }

  protected User createUser(String applicationName, String displayName, String userId) {
    User user = new User();
    user.displayName = displayName;
    user.setUserId(userId);
    user.setVreId(applicationName);
    return user;
  }

  protected SecurityInformation createSecurityInformation(String applicationName, String displayName, String userId) {
    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn(userId);

    SecurityInformation securityInformation = new SecurityInformation();
    securityInformation.setApplicationName(applicationName);
    securityInformation.setDisplayName(displayName);
    securityInformation.setPrincipal(principal);
    return securityInformation;
  }

  @Test
  public void testCreateSecurityContextUnknownUser() throws IOException {
    String applicationName = "test";
    String displayName = "displayName";
    String userId = "test123";

    SecurityInformation securityInformation = createSecurityInformation(applicationName, displayName, userId);
    User user = createUser(applicationName, displayName, userId);
    user.setRoles(Lists.newArrayList("UNVERIFIED_USER"));

    when(storageManager.searchEntity(Matchers.<Class<User>> any(), any(User.class))).thenReturn(null, user);

    instance.createSecurityContext(securityInformation);

    verify(storageManager, times(2)).searchEntity(Matchers.<Class<User>> any(), any(User.class));
    verify(storageManager, times(1)).addEntity(Matchers.<Class<User>> any(), any(User.class));
  }

  @Test
  public void testCreateSecurityContextParamNull() {
    assertNull(instance.createSecurityContext(null));
  }
}