package nl.knaw.huygens.timbuctoo.tools.importer.cnw;

/*
 * #%L
 * Timbuctoo tools
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.Visitor;
import nl.knaw.huygens.timbuctoo.model.cnw.CNWPerson;
import nl.knaw.huygens.timbuctoo.tools.importer.DefaultConverter;
import nl.knaw.huygens.timbuctoo.tools.importer.RelationDTO;
import nl.knaw.huygens.timbuctoo.tools.importer.RelationTypeImporter;
import nl.knaw.huygens.timbuctoo.tools.process.Pipeline;
import nl.knaw.huygens.timbuctoo.tools.process.Progress;
import nl.knaw.huygens.timbuctoo.util.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Converts xml data of the CNW INGForms project to json.
 */
public class CNWConverter extends DefaultConverter {

  private static final Logger LOG = LoggerFactory.getLogger(CNWConverter.class);

  public static void main(String[] args) throws Exception {
    String directory = (args.length > 0) ? args[0] : "../../ingforms/netwerkverwey/ingforms/data";
    Pipeline.execute(new CNWConverter(directory));
  }

  // ---------------------------------------------------------------------------

  private static final String VRE_ID = "CNW";
  private static final String[] TEI_EXTENSIONS = { "xml" };
  // private static final String ORGANIZATIONS = "CNW-organizations.xml";

  private final File inputDir;
  //	private Set<String> relationTypeNames;
  //	private Map<String, String> pid2koppelnaam = Maps.newHashMap();
  private Map<String, String> koppelnaam2pid = Maps.newHashMap();

  public CNWConverter(String inputDirName) throws Exception {
    super(VRE_ID);

    inputDir = new File(inputDirName);
    if (inputDir.isDirectory()) {
      System.out.printf("%nImporting from %s%n", inputDir.getCanonicalPath());
    } else {
      System.out.printf("%nNot a directory: %s%n", inputDir.getAbsolutePath());
    }
  }

  @Override
  public String getDescription() {
    return "Convert CNW INGforms data";
  }

  @Override
  public void call() throws Exception {
    try {
      openLog(getClass().getSimpleName() + ".txt");

      RelationTypeImporter importer = new RelationTypeImporter();
      importer.call(RelationTypeImporter.RELATION_TYPE_DEFS);
      //			relationTypeNames = importer.getNames();

      printBoxedText("Lists");
      Map<String, Map<String, String>> listMaps = importLists();

      printBoxedText("Persons");
      convertPersons(listMaps);

    } finally {
      displayErrorSummary();
      closeLog();
    }
  }

  private void convertPersons(Map<String, Map<String, String>> listMaps) throws Exception {
    Progress progress = new Progress();
    PrintWriter out = createPrintWriter(CNWPerson.class);
    //    Set<RelationDTO> knownRelations = Sets.newHashSet();
    Map<String, String> shortDescriptionMap = Maps.newHashMap();
    try {
      Collection<File> files = FileUtils.listFiles(inputDir, TEI_EXTENSIONS, false);
      for (File file : Sets.newTreeSet(files)) {
        progress.step();
        String fileName = file.getName();
        log(".. %s%n", fileName);
        String xml = Files.readTextFromFile(file);
        //		LOG.info("xml={}", xml);
        String pid = "cnw:pers:" + fileName.replace(".xml", "");
        PersonContext personContext = new PersonContext(pid);
        Visitor visitor = new PersonVisitor(personContext, listMaps);
        Document.createFromXml(xml).accept(visitor);
        CNWPerson person = personContext.person;
        koppelnaam2pid.put(person.getKoppelnaam(), person.getId());
        jsonConverter.appendTo(out, person);
        shortDescriptionMap.put(person.getKoppelnaam(), person.getShortDescription());
      }
      out.close();

      File personDescription = new File("import/CNW/person-short_description.csv");
      FileUtils.write(personDescription, "koppelnaam;korte_omschrijving\n", false);
      shortDescriptionMap.forEach((koppelnaam, description) -> {
        CharSequence data = StringEscapeUtils.escapeCsv(koppelnaam) + ";" + StringEscapeUtils.escapeCsv(description) + "\n";
        try {
          FileUtils.write(personDescription, data, true);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      });

    } finally {
      out.close();
      progress.done();
    }
  }

  private Map<String, Map<String, String>> importLists() throws Exception {
    Map<String, Map<String, String>> listMaps = Maps.newHashMap();
    Progress progress = new Progress();
    File listinputDir = new File("../../ingforms/lists");
    Collection<File> files = FileUtils.listFiles(listinputDir, TEI_EXTENSIONS, false);
    for (File file : Sets.newTreeSet(files)) {
      progress.step();
      String fileName = file.getName();
      log(".. %s%n", fileName);
      String xml = Files.readTextFromFile(file);
      ListContext context = new ListContext();
      ListVisitor visitor = new ListVisitor(context);
      Document.createFromXml(xml).accept(visitor);
      listMaps.put(context.getListKey(), context.getMap());
    }
    progress.done();
    return listMaps;
  }

}
