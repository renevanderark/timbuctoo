package nl.knaw.huygens.timbuctoo.tools.util.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EnumValueFieldMetaDataGenerator extends FieldMetaDataGenerator {

  public EnumValueFieldMetaDataGenerator(TypeFacade containingType) {
    super(containingType);
  }

  @Override
  protected Map<String, Object> constructValue(Field field) {
    Map<String, Object> metadataMap = Maps.newHashMap();
    metadataMap.put(TYPE_FIELD, getTypeName(field));

    addValueToValueMap(field, metadataMap);

    return metadataMap;
  }

  private String getTypeName(Field field) {
    if (List.class.isAssignableFrom(field.getType())) {
      return "List of (String)";
    }

    return "String";
  }

  protected void addValueToValueMap(Field field, Map<String, Object> metadataMap) {
    List<String> enumValues = Lists.newArrayList();
    Class<?> type = getEnumType(field);

    for (Object value : type.getEnumConstants()) {
      enumValues.add(value.toString());
    }
    metadataMap.put(VALUE_FIELD, enumValues);
  }

  private Class<?> getEnumType(Field field) {
    if (field.getType().isEnum()) {
      return field.getType();
    }

    for (Type paramType : ((ParameterizedType) field.getGenericType()).getActualTypeArguments()) {

      if (paramType instanceof Class<?>) {
        Class<?> paramClass = (Class<?>) paramType;
        if (paramClass.isEnum()) {
          return paramClass;
        }
      }
    }
    return null;
  }
}