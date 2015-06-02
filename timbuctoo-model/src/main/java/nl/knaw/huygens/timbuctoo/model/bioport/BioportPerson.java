package nl.knaw.huygens.timbuctoo.model.bioport;

import nl.knaw.huygens.timbuctoo.model.Person;

public class BioportPerson extends Person {

  private String allNames;
  private String naam;
  private String geslachtsnaam;
  private String sortKey;
  private String searchSource;
  private String snippet;
  private String thumbnail;
  private String status;
  private String remarks;
  private String timestamp;
  private String bioportId;
  private String initial;

  private String birthday;
  private String geboortedatum;
  private String geboortejaar;
  private String geboortedatumMin;
  private String geboortedatumMax;
  private String geboorteplaats;

  private String deathday;
  private String sterfdatum;
  private String sterfjaar;
  private String sterfdatumMin;
  private String sterfdatumMax;
  private String sterfplaats;

  //private String gender
  private boolean hasIllustrations;
  private boolean hasContradictions;
  private boolean hasName;
  private boolean invisible;
  private boolean orphan;

  public String getSterfdatumMin() {
    return sterfdatumMin;
  }
  public void setSterfdatumMin(String sterfdatumMin) {
    this.sterfdatumMin = sterfdatumMin;
  }

  public boolean isOrphan() {
    return orphan;
  }

  public void setOrphan(boolean orphan) {
    this.orphan = orphan;
  }

  public boolean isInvisible() {
    return invisible;
  }

  public void setInvisible(boolean invisible) {
    this.invisible = invisible;
  }

  public boolean isHasName() {
    return hasName;
  }

  public void setHasName(boolean hasName) {
    this.hasName = hasName;
  }

  public boolean isHasContradictions() {
    return hasContradictions;
  }

  public void setHasContradictions(boolean hasContradictions) {
    this.hasContradictions = hasContradictions;
  }

  public boolean isHasIllustrations() {
    return hasIllustrations;
  }

  public void setHasIllustrations(boolean hasIllustrations) {
    this.hasIllustrations = hasIllustrations;
  }

  public String getSterfplaats() {
    return sterfplaats;
  }

  public void setSterfplaats(String sterfplaats) {
    this.sterfplaats = sterfplaats;
  }

  public String getSterfdatumMax() {
    return sterfdatumMax;
  }

  public void setSterfdatumMax(String sterfdatumMax) {
    this.sterfdatumMax = sterfdatumMax;
  }

  public String getSterfjaar() {
    return sterfjaar;
  }

  public void setSterfjaar(String sterfjaar) {
    this.sterfjaar = sterfjaar;
  }

  public String getSterfdatum() {
    return sterfdatum;
  }

  public void setSterfdatum(String sterfdatum) {
    this.sterfdatum = sterfdatum;
  }

  public String getDeathday() {
    return deathday;
  }

  public void setDeathday(String deathday) {
    this.deathday = deathday;
  }

  public String getGeboorteplaats() {
    return geboorteplaats;
  }

  public void setGeboorteplaats(String geboorteplaats) {
    this.geboorteplaats = geboorteplaats;
  }

  public String getGeboortedatumMax() {
    return geboortedatumMax;
  }

  public void setGeboortedatumMax(String geboortedatumMax) {
    this.geboortedatumMax = geboortedatumMax;
  }

  public String getGeboortedatumMin() {
    return geboortedatumMin;
  }

  public void setGeboortedatumMin(String geboortedatumMin) {
    this.geboortedatumMin = geboortedatumMin;
  }

  public String getGeboortejaar() {
    return geboortejaar;
  }

  public void setGeboortejaar(String geboortejaar) {
    this.geboortejaar = geboortejaar;
  }

  public String getGeboortedatum() {
    return geboortedatum;
  }

  public void setGeboortedatum(String geboortedatum) {
    this.geboortedatum = geboortedatum;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getInitial() {
    return initial;
  }

  public void setInitial(String initial) {
    this.initial = initial;
  }

  public String getBioportId() {
    return bioportId;
  }

  public void setBioportId(String bioportId) {
    this.bioportId = bioportId;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getSnippet() {
    return snippet;
  }

  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }

  public String getSearchSource() {
    return searchSource;
  }

  public void setSearchSource(String searchSource) {
    this.searchSource = searchSource;
  }

  public String getSortKey() {
    return sortKey;
  }

  public void setSortKey(String sortKey) {
    this.sortKey = sortKey;
  }

  public String getGeslachtsnaam() {
    return geslachtsnaam;
  }

  public void setGeslachtsnaam(String geslachtsnaam) {
    this.geslachtsnaam = geslachtsnaam;
  }

  public String getNaam() {
    return naam;
  }

  public void setNaam(String naam) {
    this.naam = naam;
  }

  public String getAllNames() {
    return allNames;
  }

  public void setAllNames(String names) {
    this.allNames = names;
  }

}
