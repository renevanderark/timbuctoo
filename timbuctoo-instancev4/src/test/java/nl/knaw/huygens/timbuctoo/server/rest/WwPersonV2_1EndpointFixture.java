package nl.knaw.huygens.timbuctoo.server.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.ResourceTestRule;
import nl.knaw.huygens.concordion.extensions.HttpExpectation;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import nl.knaw.huygens.concordion.extensions.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.concordion.integration.junit4.ConcordionRunner;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RunWith(ConcordionRunner.class)
public class WwPersonV2_1EndpointFixture extends AbstractV2_1EndpointFixture {
  @Rule
  public final ResourceTestRule resources;
  private final ObjectMapper objectMapper;
  private String pid;

  public WwPersonV2_1EndpointFixture() {
    objectMapper = new ObjectMapper();
    resources = ResourceTestRule.builder().addResource(new WwPersonCollectionV2_1EndPoint()).build();
  }

  public int getNumberOfItems(HttpResult result) {
    JsonNode jsonNode = getBody(result);
    return Lists.newArrayList(jsonNode.elements()).size();
  }

  private JsonNode getBody(HttpResult result) {
    try {
      return objectMapper.readTree(result.getBody().getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean doesNotContainResult(HttpResult resultToTest, HttpResult resultToBeContained) {
    List<String> idsToTest = getIds(resultToTest);
    List<String> idsToBeContained = getIds(resultToBeContained);

    return !idsToTest.containsAll(idsToBeContained);
  }

  private List<String> getIds(HttpResult result) {
    JsonNode body = getBody(result);
    ArrayList<String> ids = Lists.newArrayList();
    for (Iterator<JsonNode> elements = body.elements(); elements.hasNext(); ) {
      ids.add(elements.next().get("_id").textValue());
    }

    return ids;
  }

  @Override
  public String validate(HttpExpectation expectation, HttpResult reality) {
    return "";
  }

  public String getAuthenticationToken() {
    List<AbstractMap.SimpleEntry<String, String>> headers = Lists.newArrayList();
    headers.add(new AbstractMap.SimpleEntry<String, String>("Authorization", "Basic dXNlcjpwYXNzd29yZA=="));

    HttpRequest loginRequest =
      new HttpRequest("POST", "/v2.1/authenticate", headers, null, null, Lists.newArrayList());

    Response response = doHttpCommand(loginRequest);

    return response.getHeaderString("x_auth_token");
  }

  public String getRecordId(HttpResult result) {
    String[] locationHeaderArray = result.getHeaders().get("location").split("/");

    return locationHeaderArray[locationHeaderArray.length - 1];
  }

  public boolean isValidPid(String result) throws JSONException {

    return !StringUtils.isBlank(result);
  }

  public String retrievePid(HttpResult result) throws JSONException {
    int attempts = 0;
    String path =  "/v2.1/domain/wwpersons/" + getRecordId(result);
    List<AbstractMap.SimpleEntry<String, String>> headers = Lists.newArrayList();
    headers.add(new AbstractMap.SimpleEntry<String, String>("Accept", "application/json"));
    HttpRequest getRequest = new HttpRequest("GET", path, headers, null, null, Lists.newArrayList());

    while ((pid == null || pid.equalsIgnoreCase("null")) && attempts < 6) {
      Response response = doHttpCommand(getRequest);
      JSONObject data = new JSONObject(response.readEntity(String.class));
      pid = data.getString("^pid");

      attempts++;
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return pid;
  }
}