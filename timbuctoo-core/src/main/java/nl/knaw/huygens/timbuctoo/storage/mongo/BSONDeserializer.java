package nl.knaw.huygens.timbuctoo.storage.mongo;

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

import java.io.IOException;
import java.util.Map;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

public class BSONDeserializer extends JsonDeserializer<BSONObject> {

  UntypedObjectDeserializer nestedSer = new UntypedObjectDeserializer();

  @Override
  public BSONObject deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    @SuppressWarnings("unchecked")
    Map<Object, Object> x = (Map<Object, Object>) nestedSer.deserialize(jp, ctxt);
    return new BasicBSONObject(x);
  }

}
