package nl.knaw.huygens.timbuctoo.model.mapping;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.facet.IndexAnnotation;
import nl.knaw.huygens.timbuctoo.facet.IndexAnnotations;
import nl.knaw.huygens.timbuctoo.model.DerivedProperty;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.Entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static nl.knaw.huygens.timbuctoo.storage.graph.MethodHelper.getGetterName;
import static nl.knaw.huygens.timbuctoo.storage.graph.MethodHelper.getMethodByName;

public class FieldNameMapFactory {
  public <T extends DomainEntity> FieldNameMap create(Representation from, Representation to, Class<T> type) throws MappingException {
    T entity = createEntity(type);
    FieldNameMap fieldNameMap = new FieldNameMap(entity);

    for (Class<?> typeToMap = type; isEntity(typeToMap); typeToMap = typeToMap.getSuperclass()) {
      addFields(from, to, typeToMap, fieldNameMap);
      addVirtualProperties(from, to, typeToMap, fieldNameMap);
    }

    addDerivedProperties(from, to, type, fieldNameMap, entity);


    return fieldNameMap;
  }

  private void addVirtualProperties(Representation from, Representation to, Class<?> type, FieldNameMap fieldNameMap) {
    for (Method method : type.getMethods()) {
      String key = from.getFieldName(type, method);
      String value = to.getFieldName(type, method);

      addField(fieldNameMap, key, value);
    }

  }

  private <T extends DomainEntity> void addDerivedProperties(Representation from, Representation to, Class<T> type, FieldNameMap fieldNameMap, T entity) throws MappingException {

    for (DerivedProperty derivedProperty : entity.getDerivedProperties()) {
      String key = from.getFieldName(type, derivedProperty);
      String value = to.getFieldName(type, derivedProperty);

      addField(fieldNameMap, key, value);
    }

  }

  private <T extends DomainEntity> T createEntity(Class<T> type) throws MappingException {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new MappingException(type, e);
    }
  }

  private static boolean isEntity(Class<?> typeToAddFieldsFor) {
    return Entity.class.isAssignableFrom(typeToAddFieldsFor);
  }

  private void addFields(Representation from, Representation to, Class<?> type, FieldNameMap fieldNameMap) {
    for (Field field : type.getDeclaredFields()) {
      String key = from.getFieldName(type, field);
      String value = to.getFieldName(type, field);
      addField(fieldNameMap, key, value);
    }
  }

  private void addField(FieldNameMap fieldNameMap, String key, String value) {
    if (key != null && value != null) {
      fieldNameMap.put(key, value);
    }
  }

  public enum Representation {
    CLIENT {
      @Override
      protected String getFieldName(Class<?> type, Field field) {
        if (field.isAnnotationPresent(JsonProperty.class)) {
          return field.getAnnotation(JsonProperty.class).value();
        }

        Method method = getMethodByName(type, getGetterName(field));
        if (isAnnotationPresentOnMethod(method, JsonProperty.class)) {
          return method.getAnnotation(JsonProperty.class).value();
        }

        return field.getName();
      }

      @Override
      public String getFieldName(Class<?> type, DerivedProperty derivedProperty) {
        return derivedProperty.getPropertyName();
      }

      @Override
      protected String getFieldName(Class<?> type, Method method) {
        VirtualProperty annotation = method.getAnnotation(VirtualProperty.class);
        return annotation != null ? annotation.propertyName() : null;
      }
    },
    //    DATABASE, TODO implement when needed
    //    POJO,TODO implement when needed
    INDEX {
      @Override
      protected String getFieldName(Class<?> type, Field field) {
        return getFieldName(type, getGetterName(field));
      }

      @Override
      public String getFieldName(Class<?> type, DerivedProperty derivedProperty) {
        return getFieldName(type, derivedProperty.getLocalAccessor());
      }

      @Override
      protected String getFieldName(Class<?> type, Method method) {
        return getFieldName(type, method.getName());
      }

      private String getFieldName(Class<?> type, String methodName) {


        for (Class<?> typeToGetMethodFrom = type; isEntity(typeToGetMethodFrom); typeToGetMethodFrom = typeToGetMethodFrom.getSuperclass()) {
          Method method = getMethodByName(typeToGetMethodFrom, methodName);
          if (isAnnotationPresentOnMethod(method, IndexAnnotation.class)) {
            IndexAnnotation annotation = method.getAnnotation(IndexAnnotation.class);
            return annotation.isSortable() ? null : annotation.fieldName();
          }

          if (isAnnotationPresentOnMethod(method, IndexAnnotations.class)) {
            for (IndexAnnotation indexAnnotation : method.getAnnotation(IndexAnnotations.class).value()) {
              if (!indexAnnotation.isSortable()) {
                return indexAnnotation.fieldName();
              }
            }
          }
        }

        return null;
      }

    };

    protected abstract String getFieldName(Class<?> type, Field field);

    protected abstract String getFieldName(Class<?> type, DerivedProperty derivedProperty);

    protected abstract String getFieldName(Class<?> type, Method method);


    protected boolean isAnnotationPresentOnMethod(Method method, Class<? extends Annotation> annotation) {
      return method != null && method.getAnnotation(annotation) != null;
    }


  }
}
