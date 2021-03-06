package nl.knaw.huygens.timbuctoo.model.neww;

/*
 * #%L
 * Timbuctoo model
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

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.timbuctoo.model.util.Datable;

public class WWRelationRefMatcher extends CompositeMatcher<WWRelationRef> {
  protected WWRelationRefMatcher() {
  }

  public static WWRelationRefMatcher likeWWRelationRef() {
    return new WWRelationRefMatcher();
  }

  public WWRelationRefMatcher withId(String id) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("id", id) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getId();
      }
    });

    return this;
  }

  public WWRelationRefMatcher withType(String type) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("type", type) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getType();
      }
    });
    return this;
  }

  public WWRelationRefMatcher withPath(String path) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("path", path) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getPath();
      }
    });

    return this;
  }

  public WWRelationRefMatcher withRelationName(String relationName) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("relationName", relationName) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getRelationName();
      }
    });
    return this;
  }

  public WWRelationRefMatcher withRev(int rev) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, Integer>("rev", rev) {

      @Override
      protected Integer getItemValue(WWRelationRef item) {
        return item.getRev();
      }
    });
    return this;
  }

  public WWRelationRefMatcher withDisplayName(String displayName) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("displayName", displayName) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getDisplayName();
      }
    });
    return this;
  }

  public WWRelationRefMatcher withRelationId(String relationId) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, String>("relationId", relationId) {

      @Override
      protected String getItemValue(WWRelationRef item) {
        return item.getRelationId();
      }
    });
    return this;
  }

  public WWRelationRefMatcher withAccepted(boolean accepted) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, Boolean>("accepted", accepted) {

      @Override
      protected Boolean getItemValue(WWRelationRef item) {
        return item.isAccepted();
      }
    });

    return this;
  }

  public WWRelationRefMatcher withDate(Datable date) {
    this.addMatcher(new PropertyEqualityMatcher<WWRelationRef, Datable>("date", date) {

      @Override
      protected Datable getItemValue(WWRelationRef item) {
        return item.getDate();
      }
    });

    return this;
  }

}
