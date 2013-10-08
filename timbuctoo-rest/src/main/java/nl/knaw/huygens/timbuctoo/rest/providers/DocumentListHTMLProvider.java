package nl.knaw.huygens.timbuctoo.rest.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import nl.knaw.huygens.timbuctoo.config.DocTypeRegistry;
import nl.knaw.huygens.timbuctoo.model.Entity;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Provider
@Produces(MediaType.TEXT_HTML)
@Singleton
public class DocumentListHTMLProvider implements MessageBodyWriter<List<? extends Entity>> {

  private final HTMLProviderHelper helper;

  @Inject
  public DocumentListHTMLProvider(DocTypeRegistry registry, @Named("html.defaultstylesheet")
  String stylesheetLink, @Named("public_url")
  String publicURL) {
    helper = new HTMLProviderHelper(registry, stylesheetLink, publicURL);
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return helper.accept(mediaType) && accept(type) && accept(genericType);
  }

  private boolean accept(Class<?> type) {
    return List.class.isAssignableFrom(type);
  }

  private boolean accept(Type genericType) {
    Class<?> type = null;
    if (genericType instanceof ParameterizedType) {
      Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
      if (actualTypes.length == 1) {
        Type actualType = actualTypes[0];
        if (actualType instanceof Class<?>) {
          type = (Class<?>) actualType;
        } else if (actualType instanceof WildcardType) {
          Type[] bounds = ((WildcardType) actualType).getUpperBounds();
          if (bounds.length == 1 && bounds[0] instanceof Class<?>) {
            type = (Class<?>) bounds[0];
          }
        }
      }
    }
    return type != null && Entity.class.isAssignableFrom(type);
  }

  @Override
  public long getSize(List<? extends Entity> docs, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(List<? extends Entity> docs, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out)
      throws IOException, WebApplicationException {
    helper.writeHeader(out, getTitle(docs));

    JsonGenerator jgen = helper.getGenerator(out);
    ObjectWriter writer = helper.getObjectWriter(annotations);
    for (Entity doc : docs) {
      helper.write(out, "<h2>");
      helper.write(out, getDocTitle(doc));
      helper.write(out, "</h2>");
      writer.writeValue(jgen, doc);
    }

    helper.writeFooter(out);
  }

  private String getTitle(List<? extends Entity> docs) {
    if (docs == null || docs.isEmpty()) {
      return "No documents";
    } else {
      String docTypeName = docs.get(0).getClass().getSimpleName();
      return String.format("%d instances of %s", docs.size(), docTypeName);
    }
  }

  private String getDocTitle(Entity doc) {
    String name = doc.getDisplayName();
    return (name != null) ? StringEscapeUtils.escapeHtml(name) : "";
  }

}