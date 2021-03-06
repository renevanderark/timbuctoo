package nl.knaw.huygens.timbuctoo.tools.importer;

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
import java.util.List;

import nl.knaw.huygens.timbuctoo.config.TypeNames;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.util.Change;
import nl.knaw.huygens.timbuctoo.storage.StorageException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 * A sub class of the GenericDataHandler, that exports the into a json-file for later use. 
 */
public class GenericJsonFileWriter extends GenericDataHandler {

  private final String testDataDir;

  public GenericJsonFileWriter(String testDataDir) {
    this.testDataDir = testDataDir;
  }

  @Override
  protected <T extends DomainEntity> void save(Class<T> type, List<T> objects, Change change) throws StorageException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      //Make sure the type is added to the json.
      mapper.enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT, As.PROPERTY);

      File file = new File(testDataDir + TypeNames.getInternalName(type) + ".json");
      System.out.println("file: " + file.getAbsolutePath());

      // toArray is needed to make use of the TimbuctooTypeIdResolver
      mapper.writeValue(file, objects.toArray(new DomainEntity[0]));
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

}
