package nl.knaw.huygens.timbuctoo.tools.util;

/*
 * #%L
 * Timbuctoo tools
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import nl.knaw.huygens.timbuctoo.config.Configuration;
import nl.knaw.huygens.timbuctoo.config.TypeRegistry;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.util.Datable;
import nl.knaw.huygens.timbuctoo.model.util.PersonName;
import nl.knaw.huygens.timbuctoo.model.util.PersonNameComponent.Type;
import nl.knaw.huygens.timbuctoo.tools.config.ToolsInjectionModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PrototypeCreator {

  private static final Random RANDOM = new Random();
  private static final Logger LOG = LoggerFactory.getLogger(PrototypeCreator.class);

  public static void main(String[] args) throws Exception {
    Configuration config = new Configuration("config.xml");
    Injector injector = Guice.createInjector(new ToolsInjectionModule(config, true, true));
    TypeRegistry registry = injector.getInstance(TypeRegistry.class);

    PrototypeCreator creator = new PrototypeCreator();

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    for (Class<? extends DomainEntity> type : registry.getDomainEntityTypes()) {
      LOG.info("DomainEntity found {}", type.getSimpleName());
      try {
        LOG.info("instance: \n{}", mapper.writeValueAsString(creator.createInstance(type)));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
  }

  public <T> T createInstance(Class<T> type) throws InstantiationException, IllegalAccessException {
    T instance = type.newInstance();

    for (Method method : type.getMethods()) {
      try {
        setValue(method, instance);
      } catch (IllegalArgumentException e) {
        LOG.error("Illegal argument for {}.", method.getName());
      } catch (InvocationTargetException e) {
        LOG.error("invocation of {} went wrong", method.getName());
      }
    }

    for (Field field : type.getFields()) {
      try {
        setValue(field, instance);
      } catch (IllegalArgumentException e) {
        LOG.error("Illegal argument for {}.", field.getName());
      }
    }

    return instance;
  }

  private <T> void setValue(Field field, T instance) throws IllegalArgumentException, IllegalAccessException {
    if (!Modifier.isFinal(field.getModifiers())) {
      field.setAccessible(true);
      field.set(instance, generateValue(field.getType(), field.getName()));
    }
  }

  private <T> void setValue(Method method, T instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Class<?>[] paramTypes = method.getParameterTypes();
    if (paramTypes.length == 1 && !Collection.class.isAssignableFrom(paramTypes[0]) && !Map.class.isAssignableFrom(paramTypes[0]) && method.getName().startsWith("set")) {
      method.invoke(instance, generateValue(paramTypes[0], method.getName()));
    }
  }

  private Object generateValue(Class<?> type, String name) {
    if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
      return false;
    } else {
      if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
        return RANDOM.nextInt();
      } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
        return RANDOM.nextLong();
      } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
        return Math.random() * 100;
      } else if (String.class.isAssignableFrom(type)) {
        return createRandomString(name);
      } else if (Class.class.isAssignableFrom(type)) {
        return type;
      } else if (Datable.class.isAssignableFrom(type)) {
        return new Datable(createDateString());
      } else if (PersonName.class.isAssignableFrom(type)) {
        return createName();
      } else if (type.isArray()) {
        return createArray(type, name);
      }
    }

    try {
      return createInstance(type);
    } catch (InstantiationException e) {
      LOG.error("instantian exception for type {}", type.getSimpleName());
    } catch (IllegalAccessException e) {
      LOG.error("illegal access exception for type {}", type.getSimpleName());
    }

    LOG.debug("Returning null for {} of type {}", name, type.getSimpleName());
    return null;
  }

  private Object createArray(Class<?> type, String name) {
    int arraySize = 5;
    Class<?> componentType = type.getComponentType();
    Object array = Array.newInstance(componentType, arraySize);

    for (int i = 0; i < arraySize; i++) {
      Array.set(array, i, generateValue(componentType, name));
    }

    return array;
  }

  private String createDateString() {
    DateFormat format = new SimpleDateFormat("yyyyMMdd");
    return format.format(new Date());
  }

  private String createRandomString(String name) {
    return name + Math.random();
  }

  private PersonName createName() {
    PersonName personName = new PersonName();
    personName.addNameComponent(Type.FORENAME, "forename");
    personName.addNameComponent(Type.SURNAME, "surname");
    return personName;
  }

}
