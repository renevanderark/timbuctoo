package nl.knaw.huygens.repository.resources;

import nl.knaw.huygens.repository.server.security.AbstractRolesAllowedResourceFilterFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;

public class MockApisAuthorizationFilterFactory extends AbstractRolesAllowedResourceFilterFactory {

  @Inject
  public MockApisAuthorizationServerResourceFilter filter;

  @Override
  protected ResourceFilter createResourceFilter(AbstractMethod am) {
    return filter;
  }

  @Override
  protected ResourceFilter createNoSecurityResourceFilter() {
    return null;
  }

}
