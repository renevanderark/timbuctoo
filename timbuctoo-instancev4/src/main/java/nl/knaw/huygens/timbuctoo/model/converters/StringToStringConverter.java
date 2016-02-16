package nl.knaw.huygens.timbuctoo.model.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;

class StringToStringConverter implements Converter {

  @Override
  public Object jsonToTinkerpop(JsonNode json) throws IOException {
    if (json.isTextual()) {
      return json.asText("");
    } else {
      throw new IOException("should be a string.");
    }
  }

  @Override
  public JsonNode tinkerpopToJson(Object value) throws IOException {
    if (value instanceof String) {
      return JsonNodeFactory.instance.textNode((String) value);
    } else {
      throw new IOException("should be an string");
    }
  }
}