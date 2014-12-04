package nl.knaw.huygens.timbuctoo.rest.resources;

/*
 * #%L
 * Timbuctoo REST api
 * =======
 * Copyright (C) 2012 - 2014 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static nl.knaw.huygens.timbuctoo.config.Paths.DOMAIN_PREFIX;
import static nl.knaw.huygens.timbuctoo.config.Paths.ENTITY_PARAM;
import static nl.knaw.huygens.timbuctoo.config.Paths.ENTITY_PATH;
import static nl.knaw.huygens.timbuctoo.config.Paths.ID_PARAM;
import static nl.knaw.huygens.timbuctoo.config.Paths.ID_PATH;
import static nl.knaw.huygens.timbuctoo.config.Paths.PID_PATH;
import static nl.knaw.huygens.timbuctoo.config.Paths.V1_PATH_OPTIONAL;
import static nl.knaw.huygens.timbuctoo.rest.util.CustomHeaders.VRE_ID_KEY;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.REVISION_KEY;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.USER_ID_KEY;
import static nl.knaw.huygens.timbuctoo.security.UserRoles.ADMIN_ROLE;
import static nl.knaw.huygens.timbuctoo.security.UserRoles.USER_ROLE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.jms.JMSException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.knaw.huygens.timbuctoo.Repository;
import nl.knaw.huygens.timbuctoo.config.TypeRegistry;
import nl.knaw.huygens.timbuctoo.messages.ActionType;
import nl.knaw.huygens.timbuctoo.messages.Broker;
import nl.knaw.huygens.timbuctoo.messages.Producer;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.Relation;
import nl.knaw.huygens.timbuctoo.model.util.Change;
import nl.knaw.huygens.timbuctoo.rest.TimbuctooException;
import nl.knaw.huygens.timbuctoo.storage.DuplicateException;
import nl.knaw.huygens.timbuctoo.storage.NoSuchEntityException;
import nl.knaw.huygens.timbuctoo.storage.StorageException;
import nl.knaw.huygens.timbuctoo.storage.UpdateException;
import nl.knaw.huygens.timbuctoo.storage.ValidationException;
import nl.knaw.huygens.timbuctoo.vre.VRE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * A REST resource for adressing collections of domain entities.
 */
@Path(V1_PATH_OPTIONAL + DOMAIN_PREFIX + "/" + ENTITY_PATH)
public class DomainEntityResource extends ResourceBase {

  private static final Logger LOG = LoggerFactory.getLogger(DomainEntityResource.class);

  protected final TypeRegistry typeRegistry;
  protected final Repository repository;
  protected final Broker broker;

  @Inject
  public DomainEntityResource(TypeRegistry registry, Repository repository, Broker broker) {
    this.typeRegistry = registry;
    this.repository = repository;
    this.broker = broker;
  }

  // --- API -----------------------------------------------------------

  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
  public Response getEntities( //
      @PathParam(ENTITY_PARAM) String entityName, //
      @QueryParam("type") String typeValue, //
      @QueryParam("rows") @DefaultValue("200") int rows, //
      @QueryParam("start") @DefaultValue("0") int start //
  ) {
    Class<? extends DomainEntity> entityType = getValidEntityType(entityName);
    List<? extends DomainEntity> list = retrieveEntities(entityType, typeValue, rows, start);
    return Response.ok(new GenericEntity<List<? extends DomainEntity>>(list) {}).build();
  }

  private <T extends DomainEntity> List<T> retrieveEntities(Class<T> entityType, String typeValue, int rows, int start) {
    if (Strings.isNullOrEmpty(typeValue)) {
      return repository.getDomainEntities(entityType).skip(start).getSome(rows);
    } else {
      return repository.getEntitiesByProperty(entityType, "type", typeValue).getAll();
    }
  }

  @SuppressWarnings("unchecked")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed({ USER_ROLE, ADMIN_ROLE })
  public <T extends DomainEntity> Response post( //
      @PathParam(ENTITY_PARAM) String entityName, //
      DomainEntity input, //
      @Context UriInfo uriInfo, //
      @HeaderParam(VRE_ID_KEY) String vreId, //
      @QueryParam(USER_ID_KEY) String userId//
  ) throws StorageException, URISyntaxException {

    Class<? extends DomainEntity> type = getValidEntityType(entityName);
    checkCondition(type == input.getClass(), BAD_REQUEST, "Type %s does not match input", type.getSimpleName());

    VRE vre = getValidVRE(vreId);
    checkCondition(vre.inScope(type), FORBIDDEN, "Type %s not in scope %s", type, vreId);

    Change change = new Change(userId, vreId);

    String id = null;
    try {
      id = repository.addDomainEntity((Class<T>) type, (T) input, change);
    } catch (DuplicateException e) {
      // TODO find a better solution
      LOG.info("Duplicate entity {} with id {}", entityName, e.getDuplicateId());
      id = updateTheDuplicateEntity(entityName, input, vreId, userId, e.getDuplicateId());
    } catch (ValidationException e) {
      throw new TimbuctooException(BAD_REQUEST, "Invalid entity; %s", e.getMessage());
    }
    notifyChange(ActionType.ADD, type, input, id);

    return Response.created(new URI(id)).build();
  }

  private String updateTheDuplicateEntity(String entityName, DomainEntity input, String vreId, String userId, String id) throws StorageException {
    Class<? extends DomainEntity> entityType = getValidEntityType(entityName);
    DomainEntity duplicatEnity = repository.getEntity(entityType, id);

    input.setRev(duplicatEnity.getRev());
    input.setId(id);

    put(entityName, id, input, vreId, userId);
    return id;
  }

  @GET
  @Path(ID_PATH)
  @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
  public DomainEntity getDoc( //
      @PathParam(ENTITY_PARAM) String entityName, //
      @PathParam(ID_PARAM) String id, //
      @QueryParam(REVISION_KEY) Integer revision//
  ) {
    Class<? extends DomainEntity> type = getValidEntityType(entityName);

    if (revision == null) {
      DomainEntity entity = repository.getEntityWithRelations(type, id);
      return checkNotNull(entity, NOT_FOUND, "No %s with id %s", type.getSimpleName(), id);
    } else {
      DomainEntity entity = repository.getRevisionWithRelations(type, id, revision);
      return checkNotNull(entity, NOT_FOUND, "No %s with id %s and revision %s", type.getSimpleName(), id, revision);
    }
  }

  @SuppressWarnings("unchecked")
  @PUT
  @Path(ID_PATH)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed({ USER_ROLE, ADMIN_ROLE })
  public <T extends DomainEntity> Response put( //
      @PathParam(ENTITY_PARAM) String entityName, //
      @PathParam(ID_PARAM) String id, //
      DomainEntity input, //
      @HeaderParam(VRE_ID_KEY) String vreId,//
      @QueryParam(USER_ID_KEY) String userId//
  ) {

    Class<? extends DomainEntity> type = getValidEntityType(entityName);
    checkCondition(type == input.getClass(), BAD_REQUEST, "Type %s does not match input", type.getSimpleName());

    DomainEntity entity = repository.getEntity(type, id);
    checkNotNull(entity, NOT_FOUND, "No %s with id %s", type.getSimpleName(), id);
    checkNotNull(entity.getPid(), FORBIDDEN, "%s with id %s is read-only (no PID)", type.getSimpleName(), id);

    VRE vre = getValidVRE(vreId);
    checkCondition(vre.inScope(type, id), FORBIDDEN, "Entity %s %s not in scope %s", type, id, vreId);

    try {
      Change change = new Change(userId, vreId);
      repository.updateDomainEntity((Class<T>) type, (T) input, change);
      notifyChange(ActionType.MOD, type, entity, id);
    } catch (NoSuchEntityException e) {
      throw new TimbuctooException(NOT_FOUND, "No %s with id %s", type.getSimpleName(), id);
    } catch (UpdateException e) {
      throw new TimbuctooException(Status.CONFLICT, "Entity %s with id %s already updated", type.getSimpleName(), id);
    } catch (StorageException e) {
      throw new TimbuctooException(INTERNAL_SERVER_ERROR, "Exception: %s", e.getMessage());
    }
    return Response.noContent().build();
  }

  @PUT
  @Path(PID_PATH)
  @RolesAllowed(ADMIN_ROLE)
  @Consumes(MediaType.APPLICATION_JSON)
  public void putPIDs(//
      @PathParam(ENTITY_PARAM) String entityName,//
      @HeaderParam(VRE_ID_KEY) String vreId) {

    Class<? extends DomainEntity> type = getValidEntityType(entityName);
    if (TypeRegistry.isPrimitiveDomainEntity(type)) {
      throw new TimbuctooException(BAD_REQUEST, "Illegal PUT for primitive domain entity %s", type.getSimpleName());
    }

    // to put a pid you must have access to the base class
    VRE vre = getValidVRE(vreId);
    Class<? extends DomainEntity> base = TypeRegistry.toBaseDomainEntity(type);
    checkCondition(vre.inScope(base), FORBIDDEN, "Type %s not in scope %s", base, vreId);

    try {
      for (String id : repository.getAllIdsWithoutPID(type)) {
        sendPersistMessage(ActionType.MOD, type, id);
      }
    } catch (StorageException e) {
      throw new TimbuctooException(INTERNAL_SERVER_ERROR, "Exception: %s", e.getMessage());
    }
  }

  @DELETE
  @Path(ID_PATH)
  @RolesAllowed({ USER_ROLE, ADMIN_ROLE })
  public Response delete( //
      @PathParam(ENTITY_PARAM) String entityName, //
      @PathParam(ID_PARAM) String id, //
      @HeaderParam(VRE_ID_KEY) String vreId) {

    Class<? extends DomainEntity> type = getValidEntityType(entityName);
    if (!TypeRegistry.isPrimitiveDomainEntity(type)) {
      throw new TimbuctooException(BAD_REQUEST, "Not a primitive domain entity: %s", entityName);
    }

    DomainEntity entity = repository.getEntity(type, id);
    checkNotNull(entity, NOT_FOUND, "No %s with id %s", type.getSimpleName(), id);
    checkNotNull(entity.getPid(), FORBIDDEN, "%s with id %s is read-only (no PID)", type, id);

    VRE vre = getValidVRE(vreId);
    checkCondition(vre.inScope(type, id), FORBIDDEN, "%s with id %s not in scope %s", type, id, vreId);

    try {
      repository.deleteDomainEntity(entity);
      notifyChange(ActionType.DEL, type, entity, id);
      return Response.status(Status.NO_CONTENT).build();
    } catch (NoSuchEntityException e) {
      throw new TimbuctooException(NOT_FOUND, "No %s with id %s", type.getSimpleName(), id);
    } catch (StorageException e) {
      throw new TimbuctooException(INTERNAL_SERVER_ERROR, "Exception: %s", e.getMessage());
    }
  }

  // --- Message handling ----------------------------------------------

  public static final String INDEX_MSG_PRODUCER = "ResourceIndexProducer";
  public static final String PERSIST_MSG_PRODUCER = "ResourcePersistProducer";

  /**
   * Notify other software components of a change in the data.
   */
  private void notifyChange(ActionType actionType, Class<? extends DomainEntity> type, DomainEntity entity, String id) {
    switch (actionType) {
      case ADD:
      case MOD:
        sendPersistMessage(actionType, type, id);
        sendIndexMessage(actionType, type, id);
        break;
      case DEL:
        sendIndexMessage(actionType, type, id);
        break;
      default:
        LOG.error("Unexpected action {}", actionType);
        break;
    }

    // TODO improve this solution
    if (Relation.class.isAssignableFrom(type)) {
      Relation relation = (Relation) entity;
      updateIndex(relation.getSourceType(), relation.getSourceId());
      updateIndex(relation.getTargetType(), relation.getTargetId());
    }
  }

  private void updateIndex(String iname, String id) {
    sendIndexMessage(ActionType.MOD, typeRegistry.getDomainEntityType(iname), id);
  }

  private void sendIndexMessage(ActionType actionType, Class<? extends DomainEntity> type, String id) {
    try {
      Producer producer = broker.getProducer(INDEX_MSG_PRODUCER, Broker.INDEX_QUEUE);
      producer.send(actionType, type, id);
    } catch (JMSException e) {
      LOG.error("Failed to send index message {} - {} - {}. \n{}", actionType, type, id, e.getMessage());
      LOG.debug("Exception", e);
    }
  }

  protected void sendPersistMessage(ActionType actionType, Class<? extends DomainEntity> type, String id) {
    try {
      Producer producer = broker.getProducer(PERSIST_MSG_PRODUCER, Broker.PERSIST_QUEUE);
      producer.send(actionType, type, id);
    } catch (JMSException e) {
      LOG.error("Failed to send persistence message {} - {} - {}. \n{}", actionType, type, id, e.getMessage());
      LOG.debug("Exception", e);
    }
  }

  // ---------------------------------------------------------------------------

  private Class<? extends DomainEntity> getValidEntityType(String name) {
    return checkNotNull(typeRegistry.getTypeForXName(name), NOT_FOUND, "No domain entity collection %s", name);
  }

  private VRE getValidVRE(String id) {
    return checkNotNull(repository.getVREById(id), NOT_FOUND, "No VRE with id %s", id);
  }

}
