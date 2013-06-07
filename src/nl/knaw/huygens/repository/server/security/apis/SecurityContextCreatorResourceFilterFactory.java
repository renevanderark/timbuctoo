package nl.knaw.huygens.repository.server.security.apis;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import nl.knaw.huygens.repository.managers.StorageManager;
import nl.knaw.huygens.repository.server.security.AbstractRolesAllowedResourceFilterFactory;

import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

/**
 * 
 * This factory creates a class, that should extract the data from the VerifyTokenResponse, added by the ApisRolesAllowedAuthenticationFilter.
 * @author martijnm
 */
public class SecurityContextCreatorResourceFilterFactory extends AbstractRolesAllowedResourceFilterFactory {
  private StorageManager storageManager;

  @Inject
  public SecurityContextCreatorResourceFilterFactory(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  @Override
  protected ResourceFilter createResourceFilter(AbstractMethod am) {
    return new SecurityContextCreatorResourceFilter(this.storageManager);
  }

  private static final class NoSecuritityFilter implements ResourceFilter, ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest request) {
      request.setSecurityContext(new SecurityContext() {

        @Override
        public boolean isUserInRole(String role) {
          return true;
        }

        @Override
        public boolean isSecure() {
          // TODO Auto-generated method stub
          return false;
        }

        @Override
        public Principal getUserPrincipal() {
          return null;
        }

        @Override
        public String getAuthenticationScheme() {
          return null;
        }
      });
      return request;
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
      return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
      return null;
    }

  }

  @Override
  protected ResourceFilter createNoSecurityResourceFilter() {
    return new NoSecuritityFilter();
  }

}
