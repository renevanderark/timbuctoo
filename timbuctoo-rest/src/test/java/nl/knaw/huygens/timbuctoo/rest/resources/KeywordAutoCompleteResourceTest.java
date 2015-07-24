package nl.knaw.huygens.timbuctoo.rest.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import nl.knaw.huygens.timbuctoo.config.Configuration;
import nl.knaw.huygens.timbuctoo.config.Paths;
import nl.knaw.huygens.timbuctoo.config.TypeNames;
import nl.knaw.huygens.timbuctoo.index.RawSearchUnavailableException;
import nl.knaw.huygens.timbuctoo.model.Keyword;
import nl.knaw.huygens.timbuctoo.rest.util.AutocompleteResultConverter;
import nl.knaw.huygens.timbuctoo.rest.util.CustomHeaders;
import nl.knaw.huygens.timbuctoo.vre.NotInScopeException;
import nl.knaw.huygens.timbuctoo.vre.SearchException;
import nl.knaw.huygens.timbuctoo.vre.VRE;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.config.Paths.DOMAIN_PREFIX;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.QUERY;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.ROWS;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.START;
import static nl.knaw.huygens.timbuctoo.rest.util.QueryParameters.TYPE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KeywordAutoCompleteResourceTest extends WebServiceTestSetup {
  private static final String DEFAULT_QUERY = "*";
  private static final int NOT_IMPLEMENTED = 501;
  protected static final Class<Keyword> KEYWORD_TYPE = Keyword.class;
  protected static final String KEYWORD_COLLECTION = TypeNames.getExternalName(KEYWORD_TYPE);
  private static final String SEARCH_PARAM = "test";
  private static final String KEY_KEY = "key";
  private static final String VALUE_KEY = "value";
  private static final String KEY_VALUE1 = "keyValue";
  private static final String VALUE_VALUE1 = "valueValue";
  private static final String KEY_VALUE2 = "keyValue";
  private static final String VALUE_VALUE2 = "valueValue";
  private static final String UNKNOWN_COLLECTION = "unknownCollections";
  private static final String EXCEPTION_MESSAGE = "Exception message";
  private static final String EXCEPTION_KEY = "exception";
  private static final int DEFAULT_START = Integer.parseInt(AutocompleteResource.DEFAULT_START);
  private static final int DEFAULT_ROWS = Integer.parseInt(AutocompleteResource.DEFAULT_ROWS);
  ;
  public static final HashMap<String, Object> NO_FILTERS = Maps.newHashMap();
  public static final String TYPE_VALUE = "typeValue";
  private URI entityURI;

  @Before
  public void setupPublicUrl() {
    entityURI = UriBuilder.fromUri(this.getBaseURI()).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).build();
    when(injector.getInstance(Configuration.class).getSetting("public_url")).thenReturn(this.getBaseURI().toString());
  }

  @Test
  public void getLetsTheAutoCompleteResultProcessorProcessARawSearchResult() throws Exception {
    // setup
    VRE vre = mock(VRE.class);
    makeVREAvailable(vre, VRE_ID);

    List<Map<String, Object>> rawSearchResult = Lists.<Map<String, Object>>newArrayList();

    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenReturn(rawSearchResult);
    convertedResultIsFoundFor(rawSearchResult);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    responseStatusIs(response, Status.OK);

    List<Map<String, Object>> entity = response.getEntity(new GenericType<List<Map<String, Object>>>() {
    });

    assertThat(entity, hasSize(2));
    verifyEntry(entity.get(0), KEY_VALUE1, VALUE_VALUE1);
    verifyEntry(entity.get(1), KEY_VALUE2, VALUE_VALUE2);

  }

  @Test
  public void getHasDoesQueryAAsteriskByDefault() throws Exception {
    // setup
    VRE vre = mock(VRE.class);
    makeVREAvailable(vre, VRE_ID);

    List<Map<String, Object>> rawSearchResult = Lists.<Map<String, Object>>newArrayList();

    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenReturn(rawSearchResult);
    convertedResultIsFoundFor(rawSearchResult);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    verify(vre).doRawSearch(KEYWORD_TYPE, DEFAULT_QUERY, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS);
  }

  @Test
  public void getInfluencesTheNumberOfResultsAndStartWhenTheQueryParametersAreSet() throws Exception {
    // setup
    int customStart = 20;
    int customRows = 50;
    VRE vre = mock(VRE.class);
    makeVREAvailable(vre, VRE_ID);

    List<Map<String, Object>> rawSearchResult = Lists.newArrayList();

    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenReturn(rawSearchResult);
    convertedResultIsFoundFor(rawSearchResult);

    // action
    resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH) //
      .queryParam(QUERY, SEARCH_PARAM).queryParam(START, "" + customStart).queryParam(ROWS, "" + customRows) //
      .header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    verify(vre).doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, customStart, customRows, NO_FILTERS);
  }

  @Test
  public void getAddsAnExtraFilterWhenTypeIsAdded() throws Exception {
    // setup
    VRE vre = mock(VRE.class);
    makeVREAvailable(vre, VRE_ID);

    List<Map<String, Object>> rawSearchResult = Lists.newArrayList();
    Map<String, Object> expectedFilters = Maps.newHashMap();
    expectedFilters.put(Keyword.INDEX_TYPE_FIELD, TYPE_VALUE);

    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenReturn(rawSearchResult);
    convertedResultIsFoundFor(rawSearchResult);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).queryParam(TYPE, TYPE_VALUE).header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    verify(vre).doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, expectedFilters);
  }


  private void convertedResultIsFoundFor(List<Map<String, Object>> rawSearchResult) {
    AutocompleteResultConverter resultConverter = injector.getInstance(AutocompleteResultConverter.class);

    ArrayList<Map<String, Object>> convertedResult = Lists.<Map<String, Object>>newArrayList();
    convertedResult.add(createEntry(KEY_VALUE1, VALUE_VALUE1));
    convertedResult.add(createEntry(KEY_VALUE2, VALUE_VALUE2));
    when(resultConverter.convert(rawSearchResult, entityURI)).thenReturn(convertedResult);
  }

  private HashMap<String, Object> createEntry(String key, String value) {
    HashMap<String, Object> entry = Maps.newHashMap();
    entry.put(KEY_KEY, key);
    entry.put(VALUE_KEY, value);
    return entry;
  }

  private void verifyEntry(Map<String, Object> entry, String key, String value) {
    assertThat(entry.keySet(), containsInAnyOrder(KEY_KEY, VALUE_KEY));
    assertThat(valueAsString(entry, KEY_KEY), is(key));
    assertThat(valueAsString(entry, VALUE_KEY), is(value));
  }

  private String valueAsString(Map<String, Object> entry, String key) {
    return "" + entry.get(key);
  }

  private void responseStatusIs(ClientResponse response, Status status) {
    assertThat(response.getStatusInfo().getStatusCode(), is(status.getStatusCode()));
  }

  @Test
  public void getReturnsNotFoundWhenNoResultsAreFound() {
    // setup
    VRE vre = mock(VRE.class);
    makeVREAvailable(vre, VRE_ID);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(UNKNOWN_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).get(ClientResponse.class);

    // verify
    responseStatusIs(response, Status.NOT_FOUND);
  }

  @Test
  public void getReturnsBadRequestWhenTheVREThrowsANotInScopeException() throws Exception {
    // setup
    VRE vre = mock(VRE.class);
    NotInScopeException exception = new NotInScopeException(KEYWORD_TYPE, VRE_ID);
    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenThrow(exception);

    makeVREAvailable(vre, VRE_ID);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    responseStatusIs(response, Status.BAD_REQUEST);
    verifyResponseHasExpectedMessage(response, exception);
  }

  @Test
  public void getReturnsInteralServerErrorWhenTheVREThrowsASearchException() throws Exception {
    VRE vre = mock(VRE.class);
    SearchException searchException = new SearchException(new Exception(EXCEPTION_MESSAGE));
    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenThrow(searchException);

    makeVREAvailable(vre, VRE_ID);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    responseStatusIs(response, Status.INTERNAL_SERVER_ERROR);
    verifyResponseHasExpectedMessage(response, searchException);

  }

  @Test
  public void getReturnsNotImplementedWhenTheVREThrowsARawSearchUnavailableException() throws Exception {
    VRE vre = mock(VRE.class);

    RawSearchUnavailableException exception = new RawSearchUnavailableException("");
    when(vre.doRawSearch(KEYWORD_TYPE, SEARCH_PARAM, DEFAULT_START, DEFAULT_ROWS, NO_FILTERS)).thenThrow(exception);

    makeVREAvailable(vre, VRE_ID);

    // action
    ClientResponse response = resource().path(Paths.V2_PATH).path(DOMAIN_PREFIX).path(KEYWORD_COLLECTION).path(Paths.AUTOCOMPLETE_PATH)//
      .queryParam(QUERY, SEARCH_PARAM).header(CustomHeaders.VRE_ID_KEY, VRE_ID).get(ClientResponse.class);

    // verify
    assertThat(response.getStatus(), is(NOT_IMPLEMENTED));
    verifyResponseHasExpectedMessage(response, "VRE with id " + VRE_ID + " does not support autocomplete on collection " + KEYWORD_COLLECTION);

  }

  protected void verifyResponseHasExpectedMessage(ClientResponse response, Exception expectedException) {
    String message = expectedException.getMessage();
    verifyResponseHasExpectedMessage(response, message);
  }

  private void verifyResponseHasExpectedMessage(ClientResponse response, String message) {
    Map<String, String> exceptionMap = response.getEntity(new GenericType<Map<String, String>>() {
    });

    assertThat(exceptionMap.keySet(), contains(EXCEPTION_KEY));
    assertThat(exceptionMap.get(EXCEPTION_KEY), is(message));
  }

}