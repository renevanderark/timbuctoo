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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 * Encapsulates the Mongo database.
 */
public class MongoDB {

  private static final Logger LOG = LoggerFactory.getLogger(MongoDB.class);

  private final Mongo mongo;
  private final DB db;

  public MongoDB(Mongo mongo, DB db) {
    this.mongo = mongo;
    this.db = db;
  }

  public void close() {
    db.cleanCursors(true);
    mongo.close();
    LOG.info("Closed");
  }

  /**
   * Gets a collection with the specified name.
   * If the collection does not exist, a new one is created.
   */
  public DBCollection getCollection(String name) {
    return db.getCollection(name);
  }

  /**
   * Inserts a document into the database.
   */
  public void insert(DBCollection collection, String id, DBObject document) throws IOException {
    collection.insert(document);
    if (collection.find(new BasicDBObject("_id", id)) == null) {
      LOG.error("Failed to insert ({}, {})", collection.getName(), id);
      throw new IOException("Insert failed");
    }
  }

  /**
   * Updates a document in the database.
   */
  public void update(DBCollection collection, DBObject query, DBObject document) throws IOException {
    WriteResult writeResult = collection.update(query, document);
    if (writeResult.getN() == 0) {
      LOG.error("Failed to update {}", query);
      throw new IOException("Update failed");
    }
  }

  /**
   * Removes documents from the database.
   */
  public int remove(DBCollection collection, DBObject query) throws IOException {
    WriteResult result = collection.remove(query);
    return (result != null) ? result.getN() : 0;
  }

  public DBCursor find(DBCollection collection, DBObject query) {
    return collection.find(query);
  }
}