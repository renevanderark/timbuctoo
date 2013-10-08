package nl.knaw.huygens.timbuctoo.model.atlg;

import java.util.List;

import nl.knaw.huygens.timbuctoo.facet.FacetType;
import nl.knaw.huygens.timbuctoo.facet.IndexAnnotation;
import nl.knaw.huygens.timbuctoo.facet.IndexAnnotations;
import nl.knaw.huygens.timbuctoo.model.EntityRef;
import nl.knaw.huygens.timbuctoo.model.Legislation;

import com.google.common.collect.Lists;

public class ATLGLegislation extends Legislation {

  /** Migration: Name of source file */
  private String origFilename;
  /** ING Forms: "Reference"; text searchable */
  private String reference;
  /** ING Forms: "Pages" */
  private String pages;
  /** ING Forms: "Short title" */
  private String titleNld;
  /** ING Forms: "English title"; text searchable */
  private String titleEng;
  /** ING Forms: "Date" */
  private String date1;
  /** ING Forms: "Date 2" */
  private String date2;
  /** ING Forms: "Keyword(s) geography"; place facet */
  private List<EntityRef> placeKeywords;
  /** ING Forms: "Keyword(s) Group classification"; subject facet */
  private List<EntityRef> groupKeywords;
  /** ING Forms: "Keyword(s) other subject"; subject facet */
  private List<EntityRef> otherKeywords;
  /** ING Forms: "Keyword(s) person"; person facet */
  private List<EntityRef> persons;
  /** ING Forms: "Summary of contents"; text searchable */
  private String contents;
  /** ING Forms: "See also" */
  private List<String> seeAlso;
  /** ING Forms: "Earlier/later publications" */
  private List<String> otherPublications;
  /** ING Forms: "Original archival source" */
  private String originalArchivalSource;
  /** ING Forms: "Link archival database" */
  private String linkArchivalDBase;
  /** ING Forms: "Remarks" */
  private String remarks;
  /** ING Forms: "Scan" */
  private String scan;
  /** ING Forms: "Parts to scan" */
  private String partsToScan;
  /** ING Forms: "Record made by-" */
  private String madeBy;
  /** ING Forms: "Reminders" */
  private String reminders;

  public ATLGLegislation() {
    placeKeywords = Lists.newArrayList();
    groupKeywords = Lists.newArrayList();
    otherKeywords = Lists.newArrayList();
    persons = Lists.newArrayList();
    seeAlso = Lists.newArrayList();
    otherPublications = Lists.newArrayList();
  }

  @Override
  public String getDisplayName() {
    return getTitleEng();
  }

  public String getOrigFilename() {
    return origFilename;
  }

  public void setOrigFilename(String origFilename) {
    this.origFilename = origFilename;
  }

  @IndexAnnotation(fieldName = "dynamic_t_text", canBeEmpty = true, isFaceted = false)
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getPages() {
    return pages;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public String getTitleNld() {
    return titleNld;
  }

  public void setTitleNld(String title) {
    this.titleNld = title;
  }

  @IndexAnnotations({ @IndexAnnotation(fieldName = "dynamic_sort_title", canBeEmpty = true, isFaceted = false, isSortable = true),
      @IndexAnnotation(fieldName = "dynamic_t_title", canBeEmpty = true, isFaceted = false, isSortable = false) })
  public String getTitleEng() {
    return titleEng;
  }

  public void setTitleEng(String title) {
    this.titleEng = title;
  }

  @IndexAnnotations({ @IndexAnnotation(fieldName = "dynamic_sort_date", canBeEmpty = true, isFaceted = false, facetType = FacetType.DATE, isSortable = true),
      @IndexAnnotation(fieldName = "dynamic_s_date", canBeEmpty = true, isFaceted = true, facetType = FacetType.DATE, isSortable = false) })
  public String getDate1() {
    return date1;
  }

  public void setDate1(String date1) {
    this.date1 = date1;
  }

  public String getDate2() {
    return date2;
  }

  public void setDate2(String date2) {
    this.date2 = date2;
  }

  @IndexAnnotation(fieldName = "dynamic_s_place", accessors = { "getDisplayName" }, canBeEmpty = true, isFaceted = true)
  public List<EntityRef> getPlaceKeywords() {
    return placeKeywords;
  }

  public void setPlaceKeywords(List<EntityRef> keywords) {
    placeKeywords = keywords;
  }

  public void addPlaceKeyword(EntityRef keyword) {
    if (keyword != null) {
      placeKeywords.add(keyword);
    }
  }

  @IndexAnnotation(fieldName = "dynamic_s_subject", accessors = { "getDisplayName" }, canBeEmpty = true, isFaceted = true)
  public List<EntityRef> getGroupKeywords() {
    return groupKeywords;
  }

  public void setGroupKeywords(List<EntityRef> keywords) {
    groupKeywords = keywords;
  }

  public void addGroupKeyword(EntityRef keyword) {
    if (keyword != null) {
      groupKeywords.add(keyword);
    }
  }

  @IndexAnnotation(fieldName = "dynamic_s_subject", accessors = { "getDisplayName" }, canBeEmpty = true, isFaceted = true)
  public List<EntityRef> getOtherKeywords() {
    return otherKeywords;
  }

  public void setOtherKeywords(List<EntityRef> keywords) {
    otherKeywords = keywords;
  }

  public void addOtherKeyword(EntityRef keyword) {
    if (keyword != null) {
      otherKeywords.add(keyword);
    }
  }

  @IndexAnnotation(fieldName = "dynamic_s_person", accessors = { "getDisplayName" }, canBeEmpty = true, isFaceted = true)
  public List<EntityRef> getPersons() {
    return persons;
  }

  public void setPersons(List<EntityRef> persons) {
    this.persons = persons;
  }

  public void addPerson(EntityRef person) {
    if (person != null) {
      persons.add(person);
    }
  }

  @IndexAnnotation(fieldName = "dynamic_t_text", canBeEmpty = true, isFaceted = false)
  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public List<String> getSeeAlso() {
    return seeAlso;
  }

  public void setSeeAlsos(List<String> seeAlso) {
    this.seeAlso = seeAlso;
  }

  public void addSeeAlso(String text) {
    seeAlso.add(text);
  }

  public List<String> getOtherPublications() {
    return otherPublications;
  }

  public void setOtherPublications(List<String> publications) {
    otherPublications = publications;
  }

  public void addOtherPublication(String publication) {
    otherPublications.add(publication);
  }

  public String getOriginalArchivalSource() {
    return originalArchivalSource;
  }

  public void setOriginalArchivalSource(String originalArchivalSource) {
    this.originalArchivalSource = originalArchivalSource;
  }

  public String getLinkArchivalDBase() {
    return linkArchivalDBase;
  }

  public void setLinkArchivalDBase(String linkArchivalDBase) {
    this.linkArchivalDBase = linkArchivalDBase;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getScan() {
    return scan;
  }

  public void setScan(String scan) {
    this.scan = scan;
  }

  public String getPartsToScan() {
    return partsToScan;
  }

  public void setPartsToScan(String partsToScan) {
    this.partsToScan = partsToScan;
  }

  public String getMadeBy() {
    return madeBy;
  }

  public void setMadeBy(String madeBy) {
    this.madeBy = madeBy;
  }

  public String getReminders() {
    return reminders;
  }

  public void setReminders(String reminders) {
    this.reminders = reminders;
  }

}