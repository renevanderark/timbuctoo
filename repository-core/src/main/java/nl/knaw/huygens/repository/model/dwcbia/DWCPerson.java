package nl.knaw.huygens.repository.model.dwcbia;

import nl.knaw.huygens.repository.facet.annotations.IndexAnnotation;
import nl.knaw.huygens.repository.model.Person;

public class DWCPerson extends Person {

  private boolean important;

  @IndexAnnotation(fieldName = "facet_b_important", isFaceted = true)
  public boolean getImportant() {
    return important;
  }

  public void setImportant(Boolean important) {
    this.important = important;
  }

}
