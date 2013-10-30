package nl.knaw.huygens.timbuctoo.index;

import java.util.List;

import nl.knaw.huygens.timbuctoo.model.Entity;

/**
 * Represents a Lucene index.
 */
public interface EntityIndex<T extends Entity> {

  void add(Class<T> docType, String docId) throws IndexException;

  void modify(Class<T> docType, String docId) throws IndexException;

  void remove(String docId) throws IndexException;

  /**
   * Remove multiple entries from the index.
   * 
   * @param ids the ids of of the entries to remove
   * @throws IndexException encapsulates the exceptions generated while deleting.
   */
  void remove(List<String> ids) throws IndexException;

  void removeAll() throws IndexException;

  void flush() throws IndexException;

}