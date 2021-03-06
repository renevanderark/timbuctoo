package test.model;

/*
 * #%L
 * Timbuctoo REST api
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

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import nl.knaw.huygens.timbuctoo.model.util.PersonName;

/**
 * Used for testing properties with various types.
 */
public class DomainEntityWithMiscTypes extends BaseDomainEntity {

  private Class<?> type;
  private Date date;
  private PersonName personName;
  private Container container;

  public DomainEntityWithMiscTypes() {
    container = new Container();
  }

  public DomainEntityWithMiscTypes(String id) {
    container = new Container();
    setId(id);
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public PersonName getPersonName() {
    return personName;
  }

  public void setPersonName(PersonName personName) {
    this.personName = personName;
  }

  public List<PersonName> getPersonNames() {
    return container.names;
  }

  public void setPersonNames(List<PersonName> personNames) {
    container.names = personNames;
  }

  public void addPersonName(PersonName personName) {
    container.names.add(personName);
  }

  // ---------------------------------------------------------------------------

  private static class Container {
    public Container() {
      names = Lists.newArrayList();
    }
    public List<PersonName> names;
  }

}
