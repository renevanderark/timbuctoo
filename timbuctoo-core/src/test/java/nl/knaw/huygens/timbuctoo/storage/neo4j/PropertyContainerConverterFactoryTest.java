package nl.knaw.huygens.timbuctoo.storage.neo4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.model.Entity;
import nl.knaw.huygens.timbuctoo.model.Relation;
import nl.knaw.huygens.timbuctoo.model.SystemEntity;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import test.model.BaseDomainEntity;
import test.model.TestSystemEntityWrapper;
import test.model.projecta.SubADomainEntity;

public class PropertyContainerConverterFactoryTest {

  private static final Class<Node> NODE_TYPE = Node.class;
  private static final Class<SubADomainEntity> DOMAIN_ENTITY_TYPE = SubADomainEntity.class;
  private static final Class<BaseDomainEntity> PRIMITIVE_DOMAIN_ENTITY_TYPE = BaseDomainEntity.class;
  private static final Class<TestSystemEntityWrapper> SYSTEM_ENTITY_TYPE = TestSystemEntityWrapper.class;
  private PropertyContainerConverterFactory instance;
  @SuppressWarnings("rawtypes")
  private NoOpPropertyContainerConverter noOpPropertyContainerConverterConverterMock;
  @SuppressWarnings("rawtypes")
  private NodeConverter nodeConverterMock;
  @SuppressWarnings("rawtypes")
  private RelationshipConverter relationshipConverterMock;
  private AbstractFieldConverter fieldConverterMock;
  private FieldConverterFactory fieldConverterFactoryMock;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    nodeConverterMock = mock(NodeConverter.class);
    noOpPropertyContainerConverterConverterMock = mock(NoOpPropertyContainerConverter.class);
    relationshipConverterMock = mock(RelationshipConverter.class);

    fieldConverterMock = mock(AbstractFieldConverter.class);
    fieldConverterFactoryMock = mock(FieldConverterFactory.class);

    when(fieldConverterFactoryMock.wrap(any(Class.class), any(Field.class))).thenReturn(fieldConverterMock);

    instance = new PropertyContainerConverterFactory(fieldConverterFactoryMock) {
      @Override
      protected <T extends Entity, U extends Node> PropertyContainerConverter<T, U> createNodeConverter(Class<T> type, Class<U> nodeType) {
        return nodeConverterMock;
      }

      @Override
      protected <T extends Entity, U extends PropertyContainer> PropertyContainerConverter<T, U> createNoOpPropertyContainerConverter(Class<T> type, Class<U> nodeType) {
        return noOpPropertyContainerConverterConverterMock;
      }

      @Override
      protected <T extends Relation, U extends Relationship> PropertyContainerConverter<T, U> createRelationshipConverter(Class<T> type, Class<U> nodeType) {
        return relationshipConverterMock;
      }
    };
  }

  @Test
  public void createForTypeAddsAFieldWrapperForEachField() throws Exception {
    // setup
    int numberOfFields = getNumberOfFields(SYSTEM_ENTITY_TYPE);
    numberOfFields += getNumberOfFields(SystemEntity.class);
    numberOfFields += getNumberOfFields(Entity.class);

    // action
    PropertyContainerConverter<TestSystemEntityWrapper, Node> propertyContainerConverter = instance.createForTypeAndPropertyContainer(SYSTEM_ENTITY_TYPE, NODE_TYPE);

    // verify
    verify(fieldConverterFactoryMock, times(numberOfFields)).wrap(argThat(equalTo(SYSTEM_ENTITY_TYPE)), any(Field.class));
    verify(propertyContainerConverter, times(numberOfFields)).addFieldConverter(fieldConverterMock);
  }

  @Test
  public void createEntityForTypeCreatesANoOpEntityConverterIfPropertyContainerIsUsed() {
    // action
    PropertyContainerConverter<TestSystemEntityWrapper, PropertyContainer> propertyContainerConverter = instance.createForTypeAndPropertyContainer(SYSTEM_ENTITY_TYPE, PropertyContainer.class);

    // verify
    assertThat(propertyContainerConverter, instanceOf(NoOpPropertyContainerConverter.class));
  }

  @Test
  public void createEntityForTypeCreatesARelationConverterIfTheEntityIsARelationAndThePropertyContainerIsARelation() {
    // action
    PropertyContainerConverter<Relation, Relationship> propertyContainerConverter = instance.createForTypeAndPropertyContainer(Relation.class, Relationship.class);

    // verify
    assertThat(propertyContainerConverter, instanceOf(RelationshipConverter.class));
  }

  @Test
  public void createEntityForTypeCreatesARegularEntityConverterIfThePropertyContainerIsANode() {
    // action
    PropertyContainerConverter<TestSystemEntityWrapper, Node> propertyContainerConverter = instance.createForTypeAndPropertyContainer(SYSTEM_ENTITY_TYPE, NODE_TYPE);

    // verify
    assertThat(propertyContainerConverter, instanceOf(PropertyContainerConverter.class));
  }

  @Test
  public void createForPrimitiveRetrievesThePrimitiveDomainEntityAndCreatesAFieldWrapperForIt() {
    // setup
    int numberOfFields = getNumberOfFields(PRIMITIVE_DOMAIN_ENTITY_TYPE);
    numberOfFields += getNumberOfFields(DomainEntity.class);
    numberOfFields += getNumberOfFields(Entity.class);

    // action
    PropertyContainerConverter<? super SubADomainEntity, Node> wrapper = instance.createForPrimitive(DOMAIN_ENTITY_TYPE, NODE_TYPE);

    // verify
    verify(fieldConverterFactoryMock, times(numberOfFields)).wrap(argThat(equalTo(PRIMITIVE_DOMAIN_ENTITY_TYPE)), any(Field.class));
    verify(wrapper, times(numberOfFields)).addFieldConverter(fieldConverterMock);
  }

  private int getNumberOfFields(Class<? extends Entity> type) {
    return type.getDeclaredFields().length;
  }
}