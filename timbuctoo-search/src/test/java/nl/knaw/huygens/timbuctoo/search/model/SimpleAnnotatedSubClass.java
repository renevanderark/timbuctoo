package nl.knaw.huygens.timbuctoo.search.model;

import nl.knaw.huygens.timbuctoo.facet.IndexAnnotation;

public class SimpleAnnotatedSubClass extends SimpleAnnotatedClass {

  private String simpleProperty;

  @IndexAnnotation(fieldName = "dynamic_s_prop", isFaceted = true, title = "Property")
  public String getSimpleProperty() {
    return simpleProperty;
  }

  public void setSimpleProperty(String simpleProperty) {
    this.simpleProperty = simpleProperty;
  }

}