package nl.knaw.huygens.timbuctoo.storage.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import nl.knaw.huygens.timbuctoo.config.DocTypeRegistry;
import nl.knaw.huygens.timbuctoo.variation.model.GeneralTestDoc;
import nl.knaw.huygens.timbuctoo.variation.model.TestConcreteDoc;
import nl.knaw.huygens.timbuctoo.variation.model.TestDocWithIDPrefix;
import nl.knaw.huygens.timbuctoo.variation.model.projecta.ProjectAGeneralTestDoc;
import nl.knaw.huygens.timbuctoo.variation.model.projectb.ProjectBGeneralTestDoc;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class MongoVariationStorageTest extends MongoStorageTestBase {

  private static final String DEFAULT_ID = "TCD000000001";

  private static DocTypeRegistry registry;

  private MongoVariationStorage storage;

  @BeforeClass
  public static void setUpDocTypeRegistry() {
    registry = new DocTypeRegistry("timbuctoo.variation.model timbuctoo.variation.model.projecta timbuctoo.variation.model.projectb");
  }

  @Override
  protected void setupStorage() throws UnknownHostException, MongoException {
    storage = new MongoVariationStorage(registry, mongo, db, DB_NAME);
    storage.setEntityIds(entityIds);
  }

  @Test
  public void testAddItem() throws IOException {
    TestConcreteDoc input = createTestDoc("test");

    Class<TestConcreteDoc> type = TestConcreteDoc.class;
    storage.addItem(type, input);

    // Two additions one normal addition and one addition in the version table.
    verify(anyCollection, times(2)).insert(any(DBObject.class));
  }

  @Test
  public void testAddItemWithIDPrefix() throws IOException {
    TestDocWithIDPrefix input = new TestDocWithIDPrefix();
    input.name = "test";
    input.generalTestDocValue = "TestDocWithIDPrefix";

    storage.addItem(TestDocWithIDPrefix.class, input);

    // Two additions one normal addition and one addition in the version table.
    verify(anyCollection, times(2)).insert(any(DBObject.class));
  }

  @Test
  public void testAddItemWithId() throws IOException {
    TestConcreteDoc input = createTestDoc(DEFAULT_ID, "test");

    Class<TestConcreteDoc> type = TestConcreteDoc.class;
    storage.addItem(type, input);

    // Two additions one normal addition and one addition in the version table.
    verify(anyCollection, times(2)).insert(any(DBObject.class));
  }

  @Test
  public void addItemSubType() throws IOException {
    GeneralTestDoc input = new GeneralTestDoc();
    input.setId(DEFAULT_ID);
    input.name = "subType";
    input.generalTestDocValue = "test";

    storage.addItem(GeneralTestDoc.class, input);

    // Two additions one normal addition and one addition in the version table.
    verify(anyCollection, times(2)).insert(any(DBObject.class));
  }

  @Test
  public void testUpdateItem() throws IOException {
    TestConcreteDoc input = createTestDoc("test");
    input.setId(DEFAULT_ID);

    when(anyCollection.findOne(any(DBObject.class))).thenReturn(createTestConcreteDocDBObject(DEFAULT_ID, "test"));

    storage.updateItem(TestConcreteDoc.class, DEFAULT_ID, input);

    //Update current version and the version collection
    verify(anyCollection, times(2)).update(any(DBObject.class), any(DBObject.class));
  }

  @Test
  public void testUpdateItemWithSubType() throws IOException {
    TestConcreteDoc input = createTestDoc("test");
    input.setId(DEFAULT_ID);

    when(anyCollection.findOne(any(DBObject.class))).thenReturn(createTestConcreteDocDBObject(DEFAULT_ID, "test"));

    ProjectAGeneralTestDoc subClassInput = new ProjectAGeneralTestDoc();
    subClassInput.name = "updated";
    subClassInput.setId(DEFAULT_ID);

    storage.updateItem(TestConcreteDoc.class, DEFAULT_ID, subClassInput);

    //Update current version and the version collection
    verify(anyCollection, times(2)).update(any(DBObject.class), any(DBObject.class));
  }

  @Test(expected = IOException.class)
  public void testUpdateItemNonExistent() throws IOException {
    TestConcreteDoc expected = createTestDoc(DEFAULT_ID, "test");
    expected.name = "updated";
    storage.updateItem(TestConcreteDoc.class, DEFAULT_ID, expected);
  }

  @Test(expected = VariationException.class)
  public void testUpdateItemMalformed() throws IOException {
    when(anyCollection.findOne(any(DBObject.class))).thenReturn(new BasicDBObject("test", "test"));

    TestConcreteDoc item = createTestDoc(DEFAULT_ID, "test");

    storage.updateItem(TestConcreteDoc.class, DEFAULT_ID, item);
  }

  @Test
  public void testDeletetem() throws IOException {
    Class<TestConcreteDoc> type = TestConcreteDoc.class;

    DBObject dbObject = createTestConcreteDocDBObject(DEFAULT_ID, "test");

    JsonNode revisionNode = mock(JsonNode.class);
    when(revisionNode.asInt()).thenReturn(1);

    ObjectNode objectNode = mock(ObjectNode.class);
    when(objectNode.isObject()).thenReturn(true);
    when(objectNode.put(anyString(), anyBoolean())).thenReturn(objectNode);
    when(objectNode.put(anyString(), anyInt())).thenReturn(objectNode);
    when(objectNode.get("^rev")).thenReturn(revisionNode);

    DBJsonNode dbJsonNode = mock(DBJsonNode.class);
    when(dbJsonNode.getDelegate()).thenReturn(objectNode);

    when(anyCollection.findOne(new BasicDBObject("_id", DEFAULT_ID))).thenReturn(dbObject);

    storage.deleteItem(type, DEFAULT_ID, null);

    // Update the current version
    // Update the version number.
    verify(anyCollection, times(2)).update(any(DBObject.class), any(DBObject.class));

  }

  @Test(expected = IOException.class)
  public void testDeleteItemNonExistent() throws IOException {
    storage.deleteItem(TestConcreteDoc.class, DEFAULT_ID, null);
  }

  @Test
  public void testGetItem() throws IOException {
    String name = "getItem";
    TestConcreteDoc expected = createTestDoc(DEFAULT_ID, name);
    expected.addVariation(TestConcreteDoc.class, DEFAULT_ID);

    DBObject dbObject = createTestConcreteDocDBObject(DEFAULT_ID, name);

    Class<TestConcreteDoc> type = TestConcreteDoc.class;

    DBObject query = new BasicDBObject();
    query.put("_id", DEFAULT_ID);
    query.put("testconcretedoc", new BasicDBObject("$ne", null));

    when(anyCollection.findOne(query)).thenReturn(dbObject);

    assertEqualDocs(expected, storage.getItem(type, DEFAULT_ID));
  }

  @Test
  public void testGetItemNonExistent() throws VariationException, IOException {
    assertNull(storage.getItem(TestConcreteDoc.class, "TCD000000001"));
  }

  @Test
  public void testGetItemSubType() throws IOException {
    GeneralTestDoc expected = new GeneralTestDoc();
    expected.setId(DEFAULT_ID);
    expected.name = "subType";
    expected.generalTestDocValue = "test";
    expected.setCurrentVariation("model");
    expected.addVariation(GeneralTestDoc.class, DEFAULT_ID);
    expected.addVariation(TestConcreteDoc.class, DEFAULT_ID);

    Class<GeneralTestDoc> type = GeneralTestDoc.class;

    DBObject value = createGeneralTestDocDBObject(DEFAULT_ID, "subType", "test");

    when(anyCollection.findOne(any(DBObject.class))).thenReturn(value);

    assertEqualDocs(expected, storage.getItem(type, DEFAULT_ID));
  }

  @Test
  public void testGetAllVariations() throws IOException {
    DBObject value = createGeneralTestDocDBObject(DEFAULT_ID, "subType", "test");
    when(anyCollection.findOne(any(DBObject.class))).thenReturn(value);

    List<TestConcreteDoc> variations = storage.getAllVariations(TestConcreteDoc.class, DEFAULT_ID);

    assertEquals(2, variations.size());
  }

  @Test
  public void testGetVariation() throws IOException {
    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    query.put("testconcretedoc", new BasicDBObject("$ne", null));

    String name = "name";
    DBObject projectAGeneralTestDBNode = createProjectAGeneralTestDBObject(DEFAULT_ID, name, "value1", "value2");

    when(anyCollection.findOne(query)).thenReturn(projectAGeneralTestDBNode);

    String variation = "projecta";
    TestConcreteDoc actual = storage.getVariation(TestConcreteDoc.class, DEFAULT_ID, variation);

    assertEquals(name, actual.name);
    assertEquals(DEFAULT_ID, actual.getId());
    assertEquals(variation, actual.getCurrentVariation());
  }

  @Test
  public void testGetVariationItemNonExisting() throws IOException {
    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    query.put("testconcretedoc", new BasicDBObject("$ne", null));

    when(anyCollection.findOne(query)).thenReturn(null);

    String variation = "projecta";
    TestConcreteDoc actual = storage.getVariation(TestConcreteDoc.class, DEFAULT_ID, variation);
    assertNull(actual);

  }

  @Test
  public void testGetVariationVariationNonExisting() throws IOException {
    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    query.put("testconcretedoc", new BasicDBObject("$ne", null));

    String name = "name";
    DBObject projectAGeneralTestDBNode = createProjectAGeneralTestDBObject(DEFAULT_ID, name, "value1", "value2");

    when(anyCollection.findOne(query)).thenReturn(projectAGeneralTestDBNode);

    TestConcreteDoc actual = storage.getVariation(TestConcreteDoc.class, DEFAULT_ID, "projectb");

    assertEquals(name, actual.name);
    assertEquals(DEFAULT_ID, actual.getId());
    assertEquals("projecta", actual.getCurrentVariation());
  }

  @Test
  public void testGetAllByType() throws IOException {
    storage.getAllByType(ProjectBGeneralTestDoc.class);

    DBObject query = new BasicDBObject("projectb-projectbgeneraltestdoc", new BasicDBObject("$ne", null));

    verify(anyCollection).find(query);
  }

  @Test
  public void testGetAllRevisions() throws IOException {
    storage.getAllRevisions(TestConcreteDoc.class, DEFAULT_ID);

    verify(anyCollection).findOne(new BasicDBObject("_id", DEFAULT_ID));
  }

  @Test
  public void testGetRevision() throws IOException {
    int revisionId = 0;
    storage.getRevision(ProjectAGeneralTestDoc.class, DEFAULT_ID, revisionId);

    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    query.put("versions.^rev", revisionId);

    verify(anyCollection).findOne(query);
  }

  @Test
  public void testGetAllIdsWithoutPIDOfType() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    String id1 = "TSD0000000001";
    DBObject dbObject = createDBJsonNode(createSimpleMap("_id", id1));

    DBCursor cursor = createDBCursorWithOneValue(dbObject);
    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    List<String> ids = storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);

    assertTrue(ids.contains(id1));

    verify(anyCollection).find(query, columnsToShow);
    verify(db).getCollection("testconcretedoc");
  }

  @Test
  public void testGetAllIdsWithoutPIDOfTypeMultipleFound() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    String id1 = DEFAULT_ID;
    DBObject dbObject1 = createDBJsonNode(createSimpleMap("_id", id1));
    String id2 = "TCD000000002";
    DBObject dbObject2 = createDBJsonNode(createSimpleMap("_id", id2));
    String id3 = "TCD000000003";
    DBObject dbObject3 = createDBJsonNode(createSimpleMap("_id", id3));

    DBCursor cursor = mock(DBCursor.class);
    when(cursor.next()).thenReturn(dbObject1, dbObject2, dbObject3);
    when(cursor.hasNext()).thenReturn(true, true, true, false);

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    List<String> ids = storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);

    assertTrue(ids.contains(id1));
    assertTrue(ids.contains(id2));
    assertTrue(ids.contains(id3));

    verify(anyCollection).find(query, columnsToShow);
    verify(db).getCollection("testconcretedoc");
  }

  @Test
  public void testGetAllIdsWithoutPIDOfTypeNoneFound() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    DBCursor cursor = createCursorWithoutValues();
    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    List<String> ids = storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);

    assertTrue(ids.isEmpty());

    verify(anyCollection).find(query, columnsToShow);
    verify(db).getCollection("testconcretedoc");
  }

  @Test(expected = IOException.class)
  public void testGetAllIdsWithoutPIDFindThrowsException() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    doThrow(MongoException.class).when(anyCollection).find(query, columnsToShow);

    storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);
  }

  @Test(expected = IOException.class)
  public void testGetAllIdsWithoutPIDCursorNextThrowsException() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    DBCursor cursor = mock(DBCursor.class);
    when(cursor.hasNext()).thenReturn(true);
    doThrow(MongoException.class).when(cursor).next();

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);
  }

  @Test(expected = IOException.class)
  public void testGetAllIdsWithoutPIDCursorHasNextThrowsException() throws IOException {
    DBObject query = new BasicDBObject("testconcretedoc", new BasicDBObject("$ne", null));
    query.put("^pid", null);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    DBCursor cursor = mock(DBCursor.class);
    doThrow(MongoException.class).when(cursor).hasNext();

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    storage.getAllIdsWithoutPIDOfType(TestConcreteDoc.class);
  }

  @Test
  public void testGetRelationIds() throws IOException {
    List<String> inputIds = Lists.newArrayList(DEFAULT_ID, "TCD000000002", "TCD000000003");

    DBObject query = createRelatedToQuery(inputIds);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    String relationId1 = "RELA000000000001";
    String relationId2 = "RELA000000000002";
    String relationId3 = "RELA000000000003";

    DBObject dbObject1 = createDBJsonNode(createSimpleMap("_id", relationId1));
    DBObject dbObject2 = createDBJsonNode(createSimpleMap("_id", relationId2));
    DBObject dbObject3 = createDBJsonNode(createSimpleMap("_id", relationId3));

    DBCursor cursor = mock(DBCursor.class);
    when(cursor.next()).thenReturn(dbObject1, dbObject2, dbObject3);
    when(cursor.hasNext()).thenReturn(true, true, true, false);

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    try {
      List<String> relationsIds = storage.getRelationIds(inputIds);

      assertTrue(relationsIds.contains(relationId1));
      assertTrue(relationsIds.contains(relationId2));
      assertTrue(relationsIds.contains(relationId3));
    } finally {
      verify(anyCollection).find(query, columnsToShow);
      verify(db).getCollection("relation");
    }
  }

  @Test(expected = IOException.class)
  public void testGetRelationFindThrowsException() throws IOException {
    List<String> inputIds = Lists.newArrayList(DEFAULT_ID, "TCD000000002", "TCD000000003");
    doThrow(MongoException.class).when(anyCollection).find(any(DBObject.class), any(DBObject.class));

    storage.getRelationIds(inputIds);
  }

  @Test(expected = IOException.class)
  public void testGetRelationCursorNextThrowsException() throws IOException {
    List<String> inputIds = Lists.newArrayList(DEFAULT_ID, "TCD000000002", "TCD000000003");

    DBObject query = createRelatedToQuery(inputIds);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    DBCursor cursor = mock(DBCursor.class);
    when(cursor.hasNext()).thenReturn(true);
    doThrow(MongoException.class).when(cursor).next();

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    storage.getRelationIds(inputIds);

  }

  @Test(expected = IOException.class)
  public void testGetRelationCursorHasNextThrowsException() throws IOException {
    List<String> inputIds = Lists.newArrayList(DEFAULT_ID, "TCD000000002", "TCD000000003");

    DBObject query = createRelatedToQuery(inputIds);
    DBObject columnsToShow = new BasicDBObject("_id", 1);

    DBCursor cursor = mock(DBCursor.class);
    doThrow(MongoException.class).when(cursor).hasNext();

    when(anyCollection.find(query, columnsToShow)).thenReturn(cursor);

    storage.getRelationIds(inputIds);
  }

  protected DBObject createRelatedToQuery(List<String> inputIds) {
    //You can not use DBQuery.or or DBQuery.in in tests, because the equals will fail.
    BasicDBObject inCollection = new BasicDBObject("$in", inputIds);
    DBObject sourceIdIn = new BasicDBObject("^sourceId", inCollection);
    DBObject targetIdIn = new BasicDBObject("^targetId", inCollection);
    DBObject query = new BasicDBObject("$or", Lists.newArrayList(sourceIdIn, targetIdIn));
    return query;
  }

  @Test
  public void testRemovePermanently() throws IOException {
    List<String> ids = Lists.newArrayList("TCD000000001", "TCD000000003", "TCD000000005");
    DBObject query = new BasicDBObject("_id", new BasicDBObject("$in", ids));
    query.put("^pid", null);

    storage.removeNonPersistent(TestConcreteDoc.class, ids);

    verify(anyCollection).remove(query);
    verify(db).getCollection("testconcretedoc");
  }

  @Test(expected = IOException.class)
  public void testRemovePemanentlyDBThrowsException() throws IOException {
    List<String> ids = Lists.newArrayList("TCD000000001", "TCD000000003", "TCD000000005");
    DBObject query = new BasicDBObject("_id", new BasicDBObject("$in", ids));
    query.put("^pid", null);
    doThrow(MongoException.class).when(anyCollection).remove(query);

    storage.removeNonPersistent(TestConcreteDoc.class, ids);

  }

  @Test
  public void testSetPID() {
    Class<ProjectAGeneralTestDoc> type = ProjectAGeneralTestDoc.class;

    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    String pid = "3c08c345-c80d-44e2-a377-029259b662b9";
    DBObject update = new BasicDBObject("$set", new BasicDBObject("^pid", pid));

    storage.setPID(type, DEFAULT_ID, pid);

    verify(anyCollection).update(query, update);
    verify(db).getCollection("testconcretedoc");
  }

  @Test(expected = RuntimeException.class)
  public void testSetPIDMongoException() {
    Class<ProjectAGeneralTestDoc> type = ProjectAGeneralTestDoc.class;

    DBObject query = new BasicDBObject("_id", DEFAULT_ID);
    String pid = "3c08c345-c80d-44e2-a377-029259b662b9";
    DBObject update = new BasicDBObject("$set", new BasicDBObject("^pid", pid));

    doThrow(MongoException.class).when(anyCollection).update(query, update);

    try {
      storage.setPID(type, DEFAULT_ID, pid);
    } finally {
      verify(db).getCollection("testconcretedoc");
    }
  }

  private TestConcreteDoc createTestDoc(String name) {
    return createTestDoc(null, name);
  }

  private TestConcreteDoc createTestDoc(String id, String name) {
    TestConcreteDoc expected = new TestConcreteDoc();
    expected.name = name;
    expected.setId(id);
    expected.setCurrentVariation("model");
    return expected;
  }

  private DBObject createTestConcreteDocDBObject(String id, String name) {
    Map<String, Object> map = createDefaultMap(id);
    map.put("testconcretedoc", createTestConcreteDocMap(name, "model"));

    DBJsonNode dbObject = createDBJsonNode(map);

    return dbObject;
  }

  private DBObject createGeneralTestDocDBObject(String id, String name, String generalTestDocValue) {
    Map<String, Object> map = createDefaultMap(id);
    Map<String, Object> testConcreteDocMap = createTestConcreteDocMap(name, "model");

    Map<String, Object> generalTestDocMap = createGeneralTestDocMap(generalTestDocValue, "model");

    map.put("testconcretedoc", testConcreteDocMap);
    map.put("generaltestdoc", generalTestDocMap);

    return createDBJsonNode(map);
  }

  private DBObject createProjectAGeneralTestDBObject(String id, String name, String generalTestDocValue, String projectAGeneralTestDocValue) {
    Map<String, Object> map = createDefaultMap(id);
    Map<String, Object> testConcreteDocMap = createTestConcreteDocMap(name, "projecta");
    Map<String, Object> generalTestDocMap = createGeneralTestDocMap(generalTestDocValue, "projecta");
    Map<String, Object> projectAGeneralTestDocMap = createProjectAGeneralTestDocMap(projectAGeneralTestDocValue);

    map.put("testconcretedoc", testConcreteDocMap);
    map.put("generaltestdoc", generalTestDocMap);
    map.put("projecta-projectageneraltestdoc", projectAGeneralTestDocMap);

    return createDBJsonNode(map);
  }

  private Map<String, Object> createProjectAGeneralTestDocMap(String projectAGeneralTestDocValue) {
    Map<String, Object> generalTestDocMap = Maps.newHashMap();
    generalTestDocMap.put("projectAGeneralTestDocValue", projectAGeneralTestDocValue);

    return generalTestDocMap;
  }

  private Map<String, Object> createGeneralTestDocMap(String generalTestDocValue, String variation) {
    Map<String, Object> generalTestDocMap = Maps.newHashMap();
    generalTestDocMap.put("generalTestDocValue", new Object[] { createValueMap(generalTestDocValue, variation) });
    generalTestDocMap.put("!defaultVRE", variation);
    return generalTestDocMap;
  }

  private Map<String, Object> createTestConcreteDocMap(String name, String variation) {

    Map<String, Object> nameMap = createValueMap(name, variation);

    Map<String, Object> testConcreteDocMap = Maps.newHashMap();
    testConcreteDocMap.put("name", new Object[] { nameMap });
    testConcreteDocMap.put("!defaultVRE", variation);
    return testConcreteDocMap;
  }

  protected Map<String, Object> createValueMap(String name, String variation) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("v", name);
    map.put("a", new String[] { variation });
    return map;
  }

  private Map<String, Object> createSimpleMap(String id, Object value) {
    Map<String, Object> map = Maps.newHashMap();
    map.put(id, value);
    return map;
  }

  private Map<String, Object> createDefaultMap(String id) {
    Map<String, Object> map = Maps.newHashMap();
    map.put("_id", id);
    map.put("^rev", 0);
    map.put("^lastChange", null);
    map.put("^creation", null);
    map.put("^pid", null);
    map.put("^deleted", false);
    return map;
  }

  /**
   * Creates a JsonNode from a map. This is used is several tests.
   */
  private DBJsonNode createDBJsonNode(Map<String, Object> map) {
    ObjectMapper mapper = new ObjectMapper();
    return new DBJsonNode(mapper.valueToTree(map));
  }

}