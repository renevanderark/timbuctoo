package nl.knaw.huygens.repository.variation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;

import org.mongojack.internal.stream.JacksonDBObject;

import nl.knaw.huygens.repository.model.Document;
import nl.knaw.huygens.repository.storage.mongo.variation.DBJsonNode;

public class VariationInducer {
  private ObjectMapper mapper;
  private Class<?> view;
  private ObjectWriter writerWithView;

  public VariationInducer() {
    mapper = new ObjectMapper();
    setView(null);
    writerWithView = mapper.writerWithView(getView());
  }
  
  public VariationInducer(ObjectMapper mapper) {
    this.mapper = mapper;
    this.setView(null);
    writerWithView = mapper.writerWithView(getView());
  }
  
  public VariationInducer(ObjectMapper mapper, Class<?> view) {
    this.mapper = mapper;
    this.setView(view);
    writerWithView = mapper.writerWithView(view);
  }
  
  public <T extends Document> JsonNode induce(T item, Class<T> cls) throws VariationException {
    return induce(item, cls, (ObjectNode) null);
  }
  
  public <T extends Document> List<JsonNode> induce(List<T> items, Class<T> cls, Map<String, DBObject> existingItems) throws VariationException {
    List<JsonNode> rv = Lists.newArrayListWithCapacity(items.size());
    for (T item : items) {
      rv.add(induce(item, cls, existingItems.get(item.getId())));
    }
    return rv;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Document> JsonNode induce(T item, Class<T> cls, DBObject existingItem) throws VariationException {
    ObjectNode o;
    if (existingItem == null) {
      o = mapper.createObjectNode();
      o.put(VariationUtils.COMMON_PROPS, mapper.createObjectNode());
    } else if (existingItem instanceof JacksonDBObject) {
      o = (ObjectNode) (((JacksonDBObject<JsonNode>) existingItem).getObject());
    } else if (existingItem instanceof DBJsonNode) {
      o = (ObjectNode) ((DBJsonNode) existingItem).getDelegate();
    } else {
      throw new VariationException("Unknown type of DBObject!");
    }
    return induce(item, cls, o);
  }

  
  public <T extends Document> JsonNode induce(T item, Class<T> cls, ObjectNode existingItem) throws VariationException {
    Class<?> commonClass = VariationUtils.getFirstCommonClass(cls);
    JsonNode commonTree = asTree(item, commonClass);
    JsonNode completeTree = asTree(item, cls);
    if (existingItem == null) {
      existingItem = mapper.createObjectNode();
      existingItem.put(VariationUtils.COMMON_PROPS, mapper.createObjectNode());
    }
    JsonNode existingCommonNode = existingItem.get(VariationUtils.COMMON_PROPS);
    if (existingCommonNode == null || !existingCommonNode.isObject()) {
      throw new VariationException("Common object is not an object?");
    }
    ObjectNode existingCommonTree = (ObjectNode) existingCommonNode;
    
    String variation = VariationUtils.getVariationName(cls);
    if (!existingItem.has(variation)) {
      existingItem.put(variation, mapper.createObjectNode());
    }
    JsonNode existingVariation = existingItem.get(variation);
    if (!existingVariation.isObject()) {
      throw new VariationException("Variation object (" + variation + ") is not an object?");
    }
    ObjectNode variationNode = (ObjectNode) existingVariation;
    
    Iterator<Entry<String, JsonNode>> fields = completeTree.fields();
    while (fields.hasNext()) {
      Entry<String, JsonNode> field = fields.next();
      String k = field.getKey();
      JsonNode fieldNode = field.getValue();
      /* For each property, there are 3 possibilities:
       * a) it is a prefixed (^ or _) property, which should always be the same among all variations
       *    and is used for identifying different objects, their version, etc.
       * b) it is common between different variations (project/VRE/whatever)
       * c) it is specific to a single variation (project/VRE/whatever)
       */
      if (k.startsWith("^") || k.startsWith("_")) {
        // Either this is a new object and we need to add the property, or it is an existing one in which
        // case we should check for an exact match:
        if (!existingItem.has(k)) {
          existingItem.put(k, fieldNode);
        } else if (!fieldNode.equals(existingItem.get(k))) {
          throw new VariationException("Inducing object into wrong object; fields " + k + " are not equal (" +
                                       fieldNode.toString() + " vs. " + existingItem.get(k).toString() + "!");
        }
      } else if (commonTree.has(k)) {
        addOrMergeVariation(existingCommonTree, k, variation, fieldNode);
      } else {
        variationNode.put(k, fieldNode);
      }
    }
    return existingItem;
  }
  
  private void addOrMergeVariation(ObjectNode existingCommonTree, String key, String variationId, JsonNode variationValue) throws VariationException {
    // Find the right property variation array, create it if it does not exist yet:
    if (!existingCommonTree.has(key)) {
      addVariation(existingCommonTree, key);
    }
    ArrayNode existingValueAry = cautiousGetArray(existingCommonTree, key);
    
    // Look through the array and remove us from things we no longer agree with, add to the thing we do agree with:
    int i = 0;
    boolean foundValue = false;
    boolean foundKey = false;
    Iterator<JsonNode> elements = existingValueAry.elements();
    while (elements.hasNext()) {
      JsonNode value = elements.next();
      if (!value.isObject()) {
        throw new VariationException("Variation for '" + key + "', index " + i + " is not an object?!");
      }

      JsonNode actualValue = value.get(VariationUtils.VALUE);
      ArrayNode agreedValueAry = cautiousGetArray(value, VariationUtils.AGREED);
      boolean thisValueIsCorrect = actualValue.equals(variationValue);
      foundValue = foundValue || thisValueIsCorrect;

      int agreedIndex = arrayIndexOf(agreedValueAry, variationId);
      // Are we currently listed as agreeing with this?
      if (agreedIndex != -1) {
        // ... while we shouldn't?
        if (!thisValueIsCorrect) {
          agreedValueAry.remove(agreedIndex);
          
          // If nobody agrees with this value anymore; purge it:
          if (agreedValueAry.size() == 0) {
            elements.remove();
          }
        }
        foundKey = true;
      } else if (thisValueIsCorrect) {
        // we're not listed but we should be:
        agreedValueAry.add(variationId);
      }
      if (foundValue && foundKey) {
        break;
      }
      i++;
    }
    // We didn't find the right value anywhere, add ourselves:
    if (!foundValue) {
      addVariationItem(existingValueAry, variationId, variationValue);
    }
  }

  private int arrayIndexOf(ArrayNode agreedValueAry, String variationId) {
    int i = agreedValueAry.size();
    while (i-- > 0) {
      JsonNode agreedValue = agreedValueAry.get(i);
      // Found us as saying we agree with this value:
      if (variationId.equals(agreedValue.asText())) {
        return i;
      }
    }
    return -1;
  }

  private ArrayNode cautiousGetArray(JsonNode obj, String key) throws VariationException {
    JsonNode val = obj.get(key);
    if (val == null || !val.isArray()) {
      throw new VariationException("Value for '" + key + "' is not an array?!");
    }
    return (ArrayNode) val;
  }

  private void addVariationItem(ArrayNode existingValueAry, String variationId, JsonNode variationValue) {
    ObjectNode var = mapper.createObjectNode();
    ArrayNode agreedList = mapper.createArrayNode();
    agreedList.add(variationId);
    var.put(VariationUtils.AGREED, agreedList);
    var.put(VariationUtils.VALUE, variationValue);
    existingValueAry.add(var);
  }

  private void addVariation(ObjectNode existingCommonTree, String key) {
    ArrayNode n = mapper.createArrayNode();
    existingCommonTree.put(key, n);
  }

  /**
   * This is a modified copy of the built-in "valueAsTree" method on ObjectMapper.
   * The modification includes being able to specify the view and the type used
   * for serialization
   * @param val Value to serialize
   * @param cls Type to use for serializing the value (should be on the type chain of the value's runtime type)
   * @return a JSON tree representation of the object
   * @throws IllegalArgumentException
   */
  private JsonNode asTree(Object val, Class<?> cls) throws IllegalArgumentException {
    if (val == null) {
      return null;
    }
    TokenBuffer buf = new TokenBuffer(mapper);
    JsonNode result;
    try {
      writerWithView.withType(cls).writeValue(buf, val);
      JsonParser jp = buf.asParser();
      result = mapper.readTree(jp);
      jp.close();
    } catch (IOException e) { // should not occur, no real i/o...
      throw new IllegalArgumentException(e.getMessage(), e);
    }
    return result;
  }

  public Class<?> getView() {
    return view;
  }

  public void setView(Class<?> view) {
    this.view = view;
  }

  public ObjectMapper getMapper() {
    return mapper;
  }

  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

}
