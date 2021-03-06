package nl.knaw.huygens.timbuctoo.rest.util;

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

/**
 * Helper class to centralize important query parameters. 
 */
public class QueryParameters {

  public static final String USER_ID_KEY = "userId";
  public static final String REVISION_KEY = "rev";
  public static final String QUERY = "query";
  public static final String ROWS = "rows";
  public static final String START = "start";
  public static final String TYPE = "type";

  private QueryParameters() {
    throw new AssertionError("Non-instantiable class");
  }
}
