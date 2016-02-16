package nl.knaw.huygens.timbuctoo.server.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.TinkerpopJsonCrudService;
import nl.knaw.huygens.timbuctoo.security.LoggedInUserStore;
import nl.knaw.huygens.timbuctoo.security.User;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@Path("/v2.1/domain/{collection}")
@Produces(MediaType.APPLICATION_JSON)
public class DomainCrudCollectionV2_1EndPoint {

  public static URI makeUrl(String collectionName) {
    return UriBuilder.fromResource(DomainCrudCollectionV2_1EndPoint.class)
      .buildFromMap(ImmutableMap.of(
        "collection", collectionName
      ));
  }

  private final TinkerpopJsonCrudService crudService;
  private final LoggedInUserStore loggedInUserStore;

  public DomainCrudCollectionV2_1EndPoint(TinkerpopJsonCrudService crudService, LoggedInUserStore loggedInUserStore) {
    this.crudService = crudService;
    this.loggedInUserStore = loggedInUserStore;
  }

  @POST
  public Response createNew(
    @PathParam("collection") String collectionName,
    @HeaderParam("Authorization") String authHeader,
    ObjectNode body
  ) throws URISyntaxException {
    Optional<User> user = loggedInUserStore.userFor(authHeader);
    if (!user.isPresent()) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } else {
      try {
        UUID id = crudService.create(collectionName, body, user.get().getId());
        return Response.created(DomainCrudEntityV2_1EndPoint.makeUrl(collectionName, id)).build();
      } catch (InvalidCollectionException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (IOException e) {
        return Response.status(400).build();
      }
    }
  }
}