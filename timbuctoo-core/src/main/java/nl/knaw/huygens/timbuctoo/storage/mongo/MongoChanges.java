package nl.knaw.huygens.timbuctoo.storage.mongo;

import java.util.List;

import nl.knaw.huygens.timbuctoo.model.Entity;
import nl.knaw.huygens.timbuctoo.storage.JsonViews;
import nl.knaw.huygens.timbuctoo.storage.RevisionChanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

public class MongoChanges<T extends Entity> implements RevisionChanges<T> {

  public MongoChanges(String id, T item) {
    this._id = id;
    this.versions = Lists.newArrayListWithExpectedSize(1);
    this.versions.add(item);
  }

  protected MongoChanges() {
    // do nothing, used for Jackson
  }

  public String _id;

  @JsonDeserialize(contentUsing = BSONDeserializer.class)
  public List<T> versions;

  @Override
  @JsonIgnore
  public String getId() {
    return _id;
  }

  @JsonView(JsonViews.WebView.class)
  @Override
  public List<T> getRevisions() {
    return versions;
  }

}