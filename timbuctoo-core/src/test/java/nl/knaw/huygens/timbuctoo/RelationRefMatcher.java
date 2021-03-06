package nl.knaw.huygens.timbuctoo;

/*
 * #%L
 * Timbuctoo core
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

import com.google.common.base.Joiner;
import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.timbuctoo.config.Paths;
import nl.knaw.huygens.timbuctoo.model.RelationRef;

public class RelationRefMatcher extends CompositeMatcher<RelationRef>{

  private RelationRefMatcher(){

  }

  public static RelationRefMatcher likeRelationRef(){
    return new RelationRefMatcher();
  }

  public RelationRefMatcher withType(String type) {
    this.addMatcher(new PropertyEqualityMatcher<RelationRef, String>("type", type) {
      @Override
      protected String getItemValue(RelationRef item) {
        return item.getType();
      }
    });

    return this;
  }

  public RelationRefMatcher withPath(String xtype, String id) {
    String path = Joiner.on('/').join(Paths.DOMAIN_PREFIX, xtype, id);

    this.addMatcher(new PropertyEqualityMatcher<RelationRef, String>("path", path) {
      @Override
      protected String getItemValue(RelationRef item) {
        return item.getPath();
      }
    });

    return this;
  }

  public RelationRefMatcher withId(String id) {
    this.addMatcher(new PropertyEqualityMatcher<RelationRef, String>("id", id) {
      @Override
      protected String getItemValue(RelationRef item) {
        return item.getId();
      }
    });

    return this;
  }
}
