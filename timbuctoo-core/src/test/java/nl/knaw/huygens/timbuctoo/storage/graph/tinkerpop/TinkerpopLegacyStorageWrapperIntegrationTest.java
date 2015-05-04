package nl.knaw.huygens.timbuctoo.storage.graph.tinkerpop;

import nl.knaw.huygens.timbuctoo.storage.DBIntegrationTestHelper;
import nl.knaw.huygens.timbuctoo.storage.StorageException;
import nl.knaw.huygens.timbuctoo.storage.StorageIntegrationTest;

import org.junit.Ignore;
import org.junit.Test;

public class TinkerpopLegacyStorageWrapperIntegrationTest extends StorageIntegrationTest {

  @Override
  protected DBIntegrationTestHelper createDBIntegrationTestHelper() {
    return new TinkerpopDBIntegrationTestHelper();
  }

  @Test
  @Override
  public void addDomainEntityAddsADomainEntityAndItsPrimitiveVersieToTheDatabase() throws Exception {
    super.addDomainEntityAddsADomainEntityAndItsPrimitiveVersieToTheDatabase();
  }

  @Test
  @Override
  public void addRelationAddsARelationAndItsPrimitiveVersionToTheDatabase() throws Exception {
    super.addRelationAddsARelationAndItsPrimitiveVersionToTheDatabase();
  }

  @Test
  @Override
  public void addSystemEntityAddsASystemEntityToTheStorageAndReturnsItsId() throws Exception {
    super.addSystemEntityAddsASystemEntityToTheStorageAndReturnsItsId();
  }

  @Test
  @Override
  public void closeClosesTheDatabaseconnection() {
    super.closeClosesTheDatabaseconnection();
  }

  @Test
  @Override
  public void countDomainEntityOnlyCountsTheLatestVersions() throws Exception {
    super.countDomainEntityOnlyCountsTheLatestVersions();
  }

  @Test
  @Override
  public void countDomainEntityReturnsTheCountOfThePrimitive() throws Exception {
    super.countDomainEntityReturnsTheCountOfThePrimitive();
  }

  @Test
  @Override
  public void countRelationsOnlyCountsTheLatest() throws Exception {
    super.countRelationsOnlyCountsTheLatest();
  }

  @Test
  @Override
  public void countSystemEntityReturnsAllTheNumberOfEntitiseOfACertainType() throws Exception {
    super.countSystemEntityReturnsAllTheNumberOfEntitiseOfACertainType();
  }

  @Ignore
  @Test
  @Override
  public void declineRelationsOfEntitySetsAcceptedToFalseForTheVariation() throws Exception {
    super.declineRelationsOfEntitySetsAcceptedToFalseForTheVariation();
  }

  @Test
  @Override
  public void deleteDomainEntityRemovesTheEntityFromTheDatabase() throws Exception {
    super.deleteDomainEntityRemovesTheEntityFromTheDatabase();
  }

  @Test
  @Override
  public void deleteNonPersistentDomainEntityRemovesTheCompleteDomainEntity() throws Exception {
    super.deleteNonPersistentDomainEntityRemovesTheCompleteDomainEntity();
  }

  @Ignore
  @Test
  @Override
  public void deleteRelationsOfEntityRemovesAllTheRelationsConnectedToTheEntity() throws Exception {
    super.deleteRelationsOfEntityRemovesAllTheRelationsConnectedToTheEntity();
  }

  @Test
  @Override
  public void deleteSystemEntityRemovesAnEntityFromTheDatabase() throws StorageException {
    super.deleteSystemEntityRemovesAnEntityFromTheDatabase();
  }

  @Ignore
  @Test
  @Override
  public void deleteVariationRemovesTheVariationFromTheEntity() throws Exception {
    super.deleteVariationRemovesTheVariationFromTheEntity();
  }

  @Test
  @Override
  public void entityExistsForDomainEntityShowsIfTheEntityExistsInTheDatabase() throws Exception {
    super.entityExistsForDomainEntityShowsIfTheEntityExistsInTheDatabase();
  }

  @Test
  @Override
  public void entityExistsForRelationShowsIfTheEntityExistsInTheDatabase() throws Exception {
    super.entityExistsForRelationShowsIfTheEntityExistsInTheDatabase();
  }

  @Test
  @Override
  public void entityExistsForSystemEntityShowsIfTheEntityExistsInTheDatabase() throws Exception {
    super.entityExistsForSystemEntityShowsIfTheEntityExistsInTheDatabase();
  }

  @Test
  @Override
  public void findItemByPropertyForDomainEntityReturnsTheFirstDomainEntityFound() throws StorageException {
    super.findItemByPropertyForDomainEntityReturnsTheFirstDomainEntityFound();
  }

  @Test
  @Override
  public void findItemByPropertyForRelationReturnsTheFirstRelationFound() throws Exception {
    super.findItemByPropertyForRelationReturnsTheFirstRelationFound();
  }

  @Test
  @Override
  public void findItemByPropertyForSystemEntityReturnsTheFirstFoundInTheDatabase() throws Exception {
    super.findItemByPropertyForSystemEntityReturnsTheFirstFoundInTheDatabase();
  }

  @Test
  @Override
  public void findRelationSearchesARelationByClassSourceIdTargetIdAndTypeId() throws Exception {
    super.findRelationSearchesARelationByClassSourceIdTargetIdAndTypeId();
  }

  @Test
  @Override
  public void getAllIdsWithoutPIDForDomainEntityReturnsTheNonPersistentDomainEntities() throws Exception {
    super.getAllIdsWithoutPIDForDomainEntityReturnsTheNonPersistentDomainEntities();
  }

  @Ignore
  @Test
  @Override
  public void getAllIdsWithoutPIDForRelationReturnsTheIdsOfNonPersistentDomainEntities() throws Exception {
    super.getAllIdsWithoutPIDForRelationReturnsTheIdsOfNonPersistentDomainEntities();
  }

  @Test
  @Override
  public void getAllVariationsReturnsAllTheVariationsOfADomainEntity() throws Exception {
    super.getAllVariationsReturnsAllTheVariationsOfADomainEntity();
  }

  @Test
  @Override
  public void getDomainEntitiesReturnsAllDomainEntitiesOfTheRequestedType() throws Exception {
    super.getDomainEntitiesReturnsAllDomainEntitiesOfTheRequestedType();
  }

  @Ignore
  @Test
  @Override
  public void getEntityOrDefaultVariationReturnsTheEntityAndItsValuesIfItExistsElseItReturnsTheEntityWithTheValuesOfTheDefaultVariation() throws Exception {
    super.getEntityOrDefaultVariationReturnsTheEntityAndItsValuesIfItExistsElseItReturnsTheEntityWithTheValuesOfTheDefaultVariation();
  }

  @Ignore
  @Test
  @Override
  public void getRelationIdsReturnsAListOfRelationIdsThatBelongToTheEntityIds() throws Exception {
    super.getRelationIdsReturnsAListOfRelationIdsThatBelongToTheEntityIds();
  }

  @Test
  @Override
  public void getRelationsByEntityIdReturnsAllTheIncomingAndOutgoingRelationsOfAnEntity() throws Exception {
    super.getRelationsByEntityIdReturnsAllTheIncomingAndOutgoingRelationsOfAnEntity();
  }

  @Test
  @Override
  public void getSystemEntitiesReturnsAllTheSystemEntitiesOfACertainType() throws Exception {
    super.getSystemEntitiesReturnsAllTheSystemEntitiesOfACertainType();
  }

  @Test
  @Override
  public void setPIDForRelationCreatesANewRevisionAndFillsThePID() throws Exception {
    super.setPIDForRelationCreatesANewRevisionAndFillsThePID();
  }

  @Test
  @Override
  public void setPIDGivesTheDomainEntityAPidAndCreatesAVersion() throws Exception {
    super.setPIDGivesTheDomainEntityAPidAndCreatesAVersion();
  }

  @Test
  @Override
  public void updateDomainEntityIncreasesTheRevisionNumberAndChangesTheDomainEntityButDoesNotCreateANewVersion() throws Exception {
    super.updateDomainEntityIncreasesTheRevisionNumberAndChangesTheDomainEntityButDoesNotCreateANewVersion();
  }

  @Test
  @Override
  public void updateDomainEntityWithADifferentTypeAddsTheNewFields() throws Exception {
    super.updateDomainEntityWithADifferentTypeAddsTheNewFields();
  }

  @Test
  @Override
  public void updateRelationUpdatesTheValuesOfTheRelationAndIncreasesTheRevButDoesNotCreateANewRevision() throws Exception {
    super.updateRelationUpdatesTheValuesOfTheRelationAndIncreasesTheRevButDoesNotCreateANewRevision();
  }

  @Test
  @Override
  public void updateSystemEntityChangesTheExistingSystemEntity() throws Exception {
    super.updateSystemEntityChangesTheExistingSystemEntity();
  }

}
