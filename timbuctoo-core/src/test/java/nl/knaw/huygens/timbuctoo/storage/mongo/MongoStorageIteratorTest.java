package nl.knaw.huygens.timbuctoo.storage.mongo;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import nl.knaw.huygens.timbuctoo.model.User;
import nl.knaw.huygens.timbuctoo.storage.StorageIterator;

import org.junit.Before;
import org.junit.Test;

public class MongoStorageIteratorTest {

  private StorageIterator<User> iterator;

  @Before
  public void setupEmptyIterator() {
    iterator = MongoStorageIterator.newInstance(User.class, null, null);
  }

  @Test
  public void testEmptyIteratorHasNext() {
    assertFalse(iterator.hasNext());
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyIteratorNext() {
    iterator.next();
  }

  @Test
  public void testEmptyIteratorSize() {
    assertEquals(0, iterator.size());
    assertEquals(0, iterator.getSome(1).size());
    assertEquals(0, iterator.getAll().size());
  }

}