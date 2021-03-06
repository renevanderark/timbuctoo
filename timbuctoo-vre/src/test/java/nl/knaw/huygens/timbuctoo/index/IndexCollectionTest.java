package nl.knaw.huygens.timbuctoo.index;

/*
 * #%L
 * Timbuctoo VRE
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;

import org.junit.Before;
import org.junit.Test;

import test.timbuctoo.index.model.BaseType1;
import test.timbuctoo.index.model.Type1;
import test.timbuctoo.index.model.Type2;

public class IndexCollectionTest {

  private static final Class<? extends DomainEntity> TYPE_WITHOUT_INDEX = Type2.class;
  private static final Class<? extends DomainEntity> TYPE_WITH_INDEX = Type1.class;
  private static final Class<? extends DomainEntity> BASE_TYPE_WITH_INDEX = BaseType1.class;
  private IndexCollection instance;

  @Before
  public void setUp() {
    instance = new IndexCollection();
    instance.addIndex(TYPE_WITH_INDEX, mock(Index.class));
  }

  @Test
  public void whenIndexCollectionHasAnIndexForTheRequestedTypeItShouldReturnIt() {
    Index index = instance.getIndexByType(TYPE_WITH_INDEX);

    assertThatIndexIsNotNullAndNotNoOpIndex(index);
  }

  @Test
  public void theIndexCollectionShouldMakeNoDifferenceBetweenPrimitivesAndProjectSpecificTypesWhenRetrievingAnIndex() {
    Index index = instance.getIndexByType(BASE_TYPE_WITH_INDEX);

    assertThatIndexIsNotNullAndNotNoOpIndex(index);
  }

  @Test
  public void whenIndexCollectionDoesNotHaveAnIndexForTheRequestedTypeItShouldReturnANoOPIndex() {
    Index index = instance.getIndexByType(TYPE_WITHOUT_INDEX);

    assertThat(index, is(notNullValue(Index.class)));
    assertThat(index, is(instanceOf(NoOpIndex.class)));
  }

  private void assertThatIndexIsNotNullAndNotNoOpIndex(Index index) {
    assertThat(index, is(notNullValue(Index.class)));
    assertThat(index, is(not(instanceOf(NoOpIndex.class))));
  }

}
