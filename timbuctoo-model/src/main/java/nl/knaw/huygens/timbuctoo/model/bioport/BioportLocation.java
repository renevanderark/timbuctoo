package nl.knaw.huygens.timbuctoo.model.bioport;

import nl.knaw.huygens.timbuctoo.model.Location;

public class BioportLocation extends Location {
  private int ufi;
  private int uni;
  private String adm1;
  private String sortName;
  private String fullName;

  public int getUfi() {
    return ufi;
  }

  public void setUfi(int ufi) {
    this.ufi = ufi;
  }

  public int getUni() {
    return uni;
  }

  public void setUni(int uni) {
    this.uni = uni;
  }

  public String getAdm1() {
    return adm1;
  }

  public void setAdm1(String adm1) {
    this.adm1 = adm1;
  }

  public String getSortName() {
    return sortName;
  }

  public void setSortName(String sortName) {
    this.sortName = sortName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
