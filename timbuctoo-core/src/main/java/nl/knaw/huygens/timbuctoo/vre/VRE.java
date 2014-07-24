package nl.knaw.huygens.timbuctoo.vre;

import java.util.List;

import nl.knaw.huygens.facetedsearch.model.parameters.FacetedSearchParameters;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.SearchResult;

/*
 * #%L
 * Timbuctoo core
 * =======
 * Copyright (C) 2012 - 2014 Huygens ING
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
 * Defines a Virtual Research Environment.
 */
public interface VRE extends Scope {

  /**
   * Returns the unique id of this VRE.
   */
  String getScopeId();

  /**
   * Returns the unique name of this VRE.
   */
  String getName();

  /**
   * Returns the unique name of this VRE.
   */
  String getDescription();

  /**
   * Returns the prefix for domain entities to derive their internal name
   * from the internal name of the primitive domain entities.
   * NOTE. This solution can be made more general.
   */
  String getDomainEntityPrefix();

  /**
   * Returns names of relation types that are considered to be receptions.
   */
  List<String> getReceptionNames();

  /**
   * Search the VRE for the items of the type of {@code entity}.
   * @param entity the type to search.
   * @param searchParameters the parameters the entity has to comply to.
   * @return the search result
   * @throws SearchException when the search request could not be processed.
   * @throws SearchValidationException when the searchParameters are not valid.
   */
  // TODO Do we want a wrapper arround FacetedSearchParameters, so we have no references to the faceted search tools library?
  <T extends FacetedSearchParameters<T>> SearchResult search(Class<? extends DomainEntity> entity, FacetedSearchParameters<T> searchParameters) throws SearchException, SearchValidationException;

}
