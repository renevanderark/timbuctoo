package nl.knaw.huygens.timbuctoo.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import nl.knaw.huygens.timbuctoo.annotations.APIDesc;
import nl.knaw.huygens.timbuctoo.config.Paths;
import nl.knaw.huygens.timbuctoo.storage.StorageManager;
import nl.knaw.huygens.timbuctoo.storage.StorageStatus;

import com.google.inject.Inject;

@Path(Paths.SYSTEM_PREFIX + "/status")
public class StatusResource {

  private final StorageManager storageManager;

  @Inject
  public StatusResource(StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @APIDesc("Returns the status of the webapp.")
  public StorageStatus getSitemap(@Context Application app) {
    return storageManager.getStatus();
  }

}