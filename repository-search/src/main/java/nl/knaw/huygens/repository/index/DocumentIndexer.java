package nl.knaw.huygens.repository.index;

import nl.knaw.huygens.repository.model.Document;

// T must be a base type
public interface DocumentIndexer<T extends Document> {

  void add(Class<T> type, String id) throws IndexException;

  void modify(Class<T> type, String id) throws IndexException;

  void remove(String id) throws IndexException;

  void removeAll() throws IndexException;

  void flush() throws IndexException;

}
