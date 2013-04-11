package nl.knaw.huygens.repository.index;

import nl.knaw.huygens.repository.indexdata.IndexAnnotation;
import nl.knaw.huygens.repository.model.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExplicitlyAnnotatedModel extends Document {

  @IndexAnnotation
  public String getString() {
    return "";
  }

  @Override
  @IndexAnnotation(fieldName = "id")
  public String getId() {
    return "";
  }

  @Override
  @JsonIgnore
  @IndexAnnotation(fieldName = "desc")
  public String getDescription() {
    // TODO Auto-generated method stub
    return "";
  }

  @Override
  @JsonProperty("!defaultVRE")
  public String getDefaultVRE() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @JsonProperty("!defaultVRE")
  public void setDefaultVRE(String defaultVRE) {
    // TODO Auto-generated method stub

  }
}
