package nl.knaw.huygens.timbuctoo.storage.mongo.variation;

import com.mongodb.DBDecoder;
import com.mongodb.DBDecoderFactory;

public class TreeDecoderFactory implements DBDecoderFactory {

  @Override
  public DBDecoder create() {
    return new TreeDecoder();
  }

}