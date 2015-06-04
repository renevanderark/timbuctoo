package nl.knaw.huygens.timbuctoo.model.bioport;

import nl.knaw.huygens.timbuctoo.model.Person;
import nl.knaw.huygens.timbuctoo.model.util.Datable;

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

  private String birthday; // coded as mmdd (no year!)
  private Datable geboortedatum;
  private Datable geboortejaar;
  private Datable geboortedatumMin;
  private Datable geboortedatumMax;
  private String geboorteplaats;

  private String deathday; // coded as mmdd (no year!)
  private Datable sterfdatum;
  private Datable sterfjaar;
  private Datable sterfdatumMin;
  private Datable sterfdatumMax;
  private String sterfplaats;

  //private String gender
  private boolean hasIllustrations;
  private boolean hasContradictions;
  private boolean hasName;
  private boolean invisible;
  private boolean orphan;

  public Datable getSterfdatumMin() {
    return sterfdatumMin;
  }

  public void setSterfdatumMin(Datable sterfdatumMin) {
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

  public Datable getSterfdatumMax() {
    return sterfdatumMax;
  }

  public void setSterfdatumMax(Datable sterfdatumMax) {
    this.sterfdatumMax = sterfdatumMax;
  }

  public Datable getSterfjaar() {
    return sterfjaar;
  }

  public void setSterfjaar(Datable sterfjaar) {
    this.sterfjaar = sterfjaar;
  }

  public Datable getSterfdatum() {
    return sterfdatum;
  }

  public void setSterfdatum(Datable sterfdatum) {
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

  public Datable getGeboortedatumMax() {
    return geboortedatumMax;
  }

  public void setGeboortedatumMax(Datable geboortedatumMax) {
    this.geboortedatumMax = geboortedatumMax;
  }

  public Datable getGeboortedatumMin() {
    return geboortedatumMin;
  }

  public void setGeboortedatumMin(Datable geboortedatumMin) {
    this.geboortedatumMin = geboortedatumMin;
  }

  public Datable getGeboortejaar() {
    return geboortejaar;
  }

  public void setGeboortejaar(Datable geboortejaar) {
    this.geboortejaar = geboortejaar;
  }

  public Datable getGeboortedatum() {
    return geboortedatum;
  }

  public void setGeboortedatum(Datable geboortedatum) {
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
