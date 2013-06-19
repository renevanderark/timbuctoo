package nl.knaw.huygens.repository;

import javax.servlet.ServletContextEvent;

import nl.knaw.huygens.repository.config.BasicInjectionModule;
import nl.knaw.huygens.repository.config.Configuration;
import nl.knaw.huygens.repository.config.ServletInjectionModule;
import nl.knaw.huygens.repository.managers.StorageManager;

import org.apache.commons.configuration.ConfigurationException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Creates and manages the injector for the repository servlet.
 */
public class RepoContextListener extends GuiceServletContextListener {

  // ** Note **
  // After stopping Tomcat one gets a message
  // "SEVERE: The web application appears to have started a thread named [com.google.inject.internal.util.$Finalizer]
  // but has failed to stop it. This is very likely to create a memory leak."
  // According to the Guice project this is just the eager Tomcat detector warning about a potential leak based
  // on it's internal introspection. Once new ThreadLocals are created the entry that causes this message is removed.
  // See: http://code.google.com/p/google-guice/issues/detail?id=707

  private Injector injector;

  @Override
  protected Injector getInjector() {
    try {
      Configuration config = new Configuration();
      Module baseModule = new BasicInjectionModule(config);
      Module servletModule = new ServletInjectionModule();
      injector = Guice.createInjector(baseModule, servletModule);
      return injector;
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    if (injector != null) {
      StorageManager storageManager = injector.getInstance(StorageManager.class);
      if (storageManager != null) {
        storageManager.close();
      }
      injector = null;
    }
  }

}
