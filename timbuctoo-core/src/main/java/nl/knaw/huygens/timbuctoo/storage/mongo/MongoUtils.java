package nl.knaw.huygens.timbuctoo.storage.mongo;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.huygens.timbuctoo.model.Entity;
import nl.knaw.huygens.timbuctoo.storage.JsonViews;

import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.object.BsonObjectGenerator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoUtils {

  private static ObjectWriter dbWriter;
  static {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_DEFAULT);
    dbWriter = mapper.writerWithView(JsonViews.DBView.class);
  }

  public static DBObject getObjectForDoc(Object doc) throws IOException {
    if (doc == null) {
      return null;
    }
    BsonObjectGenerator generator = new BsonObjectGenerator();
    dbWriter.writeValue(generator, doc);
    DBObject dbObject = generator.getDBObject();
    dbObject.removeField("@class");

    return generator.getDBObject();
  }

  public static String getCollectionName(Class<? extends Entity> type) {
    return type.getSimpleName().toLowerCase();
  }

  public static String getVersioningCollectionName(Class<? extends Entity> type) {
    return getCollectionName(type) + "-versions";
  }

  public static <T extends Entity> JacksonDBCollection<T, String> getCollection(DB db, Class<T> cls) {
    DBCollection col = db.getCollection(getCollectionName(cls));
    return JacksonDBCollection.wrap(col, cls, String.class, JsonViews.DBView.class);
  }

  public static <T extends Entity> JacksonDBCollection<MongoChanges<T>, String> getVersioningCollection(DB db, Class<T> cls) {
    DBCollection col = db.getCollection(getVersioningCollectionName(cls));
    JavaType someType = TypeFactory.defaultInstance().constructParametricType(MongoChanges.class, cls);
    return RepoJacksonDBCollection.wrap(col, someType, JsonViews.DBView.class);
  }

  public static void sortDocumentsByLastChange(List<DBObject> docs) {
    Collections.sort(docs, new Comparator<DBObject>() {
      @Override
      public int compare(DBObject o1, DBObject o2) {
        long ds1 = getDS(o1);
        long ds2 = getDS(o2);
        long d = ds2 - ds1;
        return d > 0 ? 1 : (d < 0 ? -1 : 0);
      }

      private long getDS(DBObject o1) {
        Object o1s = o1 != null ? o1.get("^lastChange") : null;
        o1s = (o1s != null && o1s instanceof DBObject) ? ((DBObject) o1s).get("^lastChange") : null;
        o1s = (o1s != null && o1s instanceof DBObject) ? ((DBObject) o1s).get("dateStamp") : null;
        return o1s != null ? (Long) o1s : -1;
      }
    });
  }

}