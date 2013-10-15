package nl.knaw.huygens.timbuctoo.storage.mongo;

import java.util.List;

import nl.knaw.huygens.timbuctoo.storage.AbstractStorageIterator;

import org.mongojack.DBCursor;

class MongoDBIterator<T> extends AbstractStorageIterator<T> {

  protected DBCursor<T> cursor;

  public MongoDBIterator(DBCursor<T> delegate) {
    super(delegate);
    cursor = delegate;
  }

  @Override
  protected void closeInternal() {
    cursor.close();
  }

  @Override
  public int size() {
    return cursor.count();
  }

  @Override
  public void skip(int count) {
    cursor.skip(count);
  }

  @Override
  public List<T> getSome(int limit) {
    return cursor.toArray(limit);
  }

}