package nl.knaw.huygens.timbuctoo.tools.importer.database;

import nl.knaw.huygens.timbuctoo.facet.IndexAnnotation;
import nl.knaw.huygens.timbuctoo.model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentExtensionWithStringField extends Entity {

  public DocumentExtensionWithStringField() {}

  private String test;

  @Override
  @JsonIgnore
  @IndexAnnotation(fieldName = "desc")
  public String getDisplayName() {
    // TODO Auto-generated method stub
    return null;
  }

  public void setTest(String test) {
    this.test = test;
  }

  public String getTest() {
    return this.test;
  }

  @Override
  @JsonProperty("!currentVariation")
  public String getCurrentVariation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @JsonProperty("!currentVariation")
  public void setCurrentVariation(String defaultVRE) {
    // TODO Auto-generated method stub

  }

}