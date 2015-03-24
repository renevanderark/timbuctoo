package nl.knaw.huygens.timbuctoo.storage.neo4j;

import static nl.knaw.huygens.timbuctoo.model.Entity.ID_PROPERTY_NAME;
import static nl.knaw.huygens.timbuctoo.storage.neo4j.NodeMockBuilder.aNode;
import static nl.knaw.huygens.timbuctoo.storage.neo4j.RelationshipMockBuilder.aRelationship;
import static nl.knaw.huygens.timbuctoo.storage.neo4j.SearchResultBuilder.aSearchResult;
import static nl.knaw.huygens.timbuctoo.storage.neo4j.SearchResultBuilder.anEmptySearchResult;
import static nl.knaw.huygens.timbuctoo.storage.neo4j.SubADomainEntityBuilder.aDomainEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import nl.knaw.huygens.timbuctoo.storage.NoSuchEntityException;
import nl.knaw.huygens.timbuctoo.storage.StorageException;

import org.junit.Test;
import org.mockito.InOrder;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import test.model.projecta.SubADomainEntity;

public class Neo4JLegacyStorageWrapperDomainEntityTest extends Neo4JLegacyStorageWrapperTest {

  private static final Class<SubADomainEntity> DOMAIN_ENTITY_TYPE = SubADomainEntity.class;

  @Test
  public void addDomainEntityDelegatesToNeo4JStorageAddDomainEntity() throws Exception {
    // setup
    SubADomainEntity entity = aDomainEntity().build();
    when(neo4JStorageMock.addDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE)).thenReturn(ID);

    // action
    String id = instance.addDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);

    // verify
    assertThat(id, is(equalTo(ID)));
    verify(neo4JStorageMock).addDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);
  }

  @Test(expected = StorageException.class)
  public void addDomainEntityThrowsAnExceptionWhenTheDelegateDoes() throws Exception {
    // setup
    SubADomainEntity entity = aDomainEntity().build();
    when(neo4JStorageMock.addDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE)).thenThrow(new StorageException());

    // action
    instance.addDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);
  }

  @Test
  public void getEntityDelegatesToNeo4JStorageGetEntity() throws Exception {
    // setup
    SubADomainEntity entity = aDomainEntity().build();
    when(neo4JStorageMock.getEntity(DOMAIN_ENTITY_TYPE, ID)).thenReturn(entity);

    // action
    SubADomainEntity actualEntity = instance.getEntity(DOMAIN_ENTITY_TYPE, ID);

    // verify
    assertThat(actualEntity, is(sameInstance(entity)));
  }

  @Test(expected = StorageException.class)
  public void getEntityDelegatesThrowsAStorageExceptionWhenTheDelegateDoes() throws Exception {
    // setup
    when(neo4JStorageMock.getEntity(DOMAIN_ENTITY_TYPE, ID)).thenThrow(new StorageException());

    // action
    instance.getEntity(DOMAIN_ENTITY_TYPE, ID);
  }

  @Test
  public void updateDomainEntityDelegatesToNeo4JStorage() throws Exception {
    // setup
    SubADomainEntity entity = aDomainEntity().build();

    // action
    instance.updateDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);

    // verify
    verify(neo4JStorageMock).updateDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);
  }

  @Test(expected = StorageException.class)
  public void updateDomainEntityThrowAStorageExceptionWhenTheDelegateDoes() throws Exception {
    // setup
    SubADomainEntity entity = aDomainEntity().build();

    doThrow(StorageException.class).when(neo4JStorageMock).updateDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);

    // action
    instance.updateDomainEntity(DOMAIN_ENTITY_TYPE, entity, CHANGE);

  }

  @Test
  public void deleteDomainEntityFirstRemovesTheNodesRelationShipsAndThenTheNodeItselfTheDatabase() throws Exception {
    // setup
    Relationship relMock1 = aRelationship().build();
    Relationship relMock2 = aRelationship().build();
    Node nodeMock = aNode().withOutgoingRelationShip(relMock1).andOutgoingRelationship(relMock2).build();

    aSearchResult().forLabel(PRIMITIVE_DOMAIN_ENTITY_LABEL).andId(ID) //
        .withNode(nodeMock) //
        .foundInDB(dbMock);

    // action
    instance.deleteDomainEntity(Neo4JLegacyStorageWrapperTest.PRIMITIVE_DOMAIN_ENTITY_TYPE, ID, CHANGE);

    // verify
    InOrder inOrder = inOrder(dbMock, nodeMock, relMock1, relMock2, transactionMock);
    inOrder.verify(dbMock).beginTx();
    verifyNodeAndItsRelationAreDelete(nodeMock, relMock1, relMock2, inOrder);
    inOrder.verify(transactionMock).success();

  }

  @Test
  public void deleteDomainEntityRemovesAllTheFoundNodes() throws Exception {
    // setup
    Relationship relMock1 = aRelationship().build();
    Relationship relMock2 = aRelationship().build();
    Node nodeMock = aNode().withOutgoingRelationShip(relMock1).andOutgoingRelationship(relMock2).build();

    Relationship relMock3 = aRelationship().build();
    Relationship relMock4 = aRelationship().build();
    Node nodeMock2 = aNode().withOutgoingRelationShip(relMock3).andOutgoingRelationship(relMock4).build();

    Relationship relMock5 = aRelationship().build();
    Relationship relMock6 = aRelationship().build();
    Node nodeMock3 = aNode().withOutgoingRelationShip(relMock5).andOutgoingRelationship(relMock6).build();

    aSearchResult().forLabel(PRIMITIVE_DOMAIN_ENTITY_LABEL).andId(ID) //
        .withNode(nodeMock) //
        .andNode(nodeMock2) //
        .andNode(nodeMock3) //
        .foundInDB(dbMock);

    // action
    instance.deleteDomainEntity(Neo4JLegacyStorageWrapperTest.PRIMITIVE_DOMAIN_ENTITY_TYPE, ID, CHANGE);

    // verify
    InOrder inOrder = inOrder(dbMock, nodeMock, relMock1, relMock2, nodeMock2, relMock3, relMock4, nodeMock3, relMock5, relMock6, transactionMock);
    inOrder.verify(dbMock).beginTx();
    verifyNodeAndItsRelationAreDelete(nodeMock, relMock1, relMock2, inOrder);
    verifyNodeAndItsRelationAreDelete(nodeMock2, relMock3, relMock4, inOrder);
    verifyNodeAndItsRelationAreDelete(nodeMock3, relMock5, relMock6, inOrder);
    inOrder.verify(transactionMock).success();
  }

  @Test(expected = NoSuchEntityException.class)
  public void deleteDomainEntityThrowsANoSuchEntityExceptionWhenTheEntityCannotBeFound() throws Exception {
    // setup
    anEmptySearchResult().forLabel(PRIMITIVE_DOMAIN_ENTITY_LABEL).andId(ID).foundInDB(dbMock);
    try {
      // action
      instance.deleteDomainEntity(Neo4JLegacyStorageWrapperTest.PRIMITIVE_DOMAIN_ENTITY_TYPE, ID, CHANGE);
    } finally {
      // verify
      verify(dbMock).beginTx();
      verify(dbMock).findNodesByLabelAndProperty(PRIMITIVE_DOMAIN_ENTITY_LABEL, ID_PROPERTY_NAME, ID);
      verify(transactionMock).failure();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void deleteThrowsAnIllegalArgumentExceptionWhenTheEntityIsNotAPrimitiveDomainEntity() throws Exception {

    try {
      // action
      instance.deleteDomainEntity(Neo4JLegacyStorageWrapperDomainEntityTest.DOMAIN_ENTITY_TYPE, ID, CHANGE);
    } finally {
      // verify
      verifyZeroInteractions(dbMock);
    }
  }

  @Test
  public void setPIDDelegatesToNeo4JStorageSetDomainEntityPID() throws Exception {
    // action
    instance.setPID(DOMAIN_ENTITY_TYPE, ID, PID);

    // verify
    verify(neo4JStorageMock).setDomainEntityPID(DOMAIN_ENTITY_TYPE, ID, PID);
  }

  @Test(expected = StorageException.class)
  public void setPIDThrowsAStorageExceptionIfTheDelegateDoes() throws Exception {
    // setup
    doThrow(StorageException.class).when(neo4JStorageMock).setDomainEntityPID(DOMAIN_ENTITY_TYPE, ID, PID);

    // action
    instance.setPID(DOMAIN_ENTITY_TYPE, ID, PID);
  }

}