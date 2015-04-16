package nl.knaw.huygens.timbuctoo.storage.neo4j.conversion;

import java.util.List;

import nl.knaw.huygens.timbuctoo.model.Entity;
import nl.knaw.huygens.timbuctoo.storage.neo4j.ConversionException;
import nl.knaw.huygens.timbuctoo.storage.neo4j.NodeConverter;

import org.neo4j.graphdb.Node;

class CompositeNodeConverter<T extends Entity> implements NodeConverter<T> {

  private List<NodeConverter<? super T>> nodeConverters;

  public CompositeNodeConverter(List<NodeConverter<? super T>> nodeConverters) {
    this.nodeConverters = nodeConverters;
  }

  @Override
  public void addValuesToPropertyContainer(Node node, T entity) throws ConversionException {
    executeAction(new ActionExecutor<T>() {
      @Override
      public void execute(NodeConverter<? super T> converter, Node node, T entity) throws ConversionException {
        converter.addValuesToPropertyContainer(node, entity);
      }
    }, node, entity);
  }

  @Override
  public void addValuesToEntity(T entity, Node node) throws ConversionException {
    executeAction(new ActionExecutor<T>() {
      @Override
      public void execute(NodeConverter<? super T> converter, Node node, T entity) throws ConversionException {
        converter.addValuesToEntity(entity, node);
      }
    }, node, entity);
  }

  @Override
  public void updatePropertyContainer(Node node, T entity) throws ConversionException {
    executeAction(new ActionExecutor<T>() {
      @Override
      public void execute(NodeConverter<? super T> converter, Node node, T entity) throws ConversionException {
        converter.updatePropertyContainer(node, entity);
      }
    }, node, entity);
  }

  @Override
  public void updateModifiedAndRev(Node node, T entity) throws ConversionException {
    executeAction(new ActionExecutor<T>() {
      @Override
      public void execute(NodeConverter<? super T> converter, Node node, T entity) throws ConversionException {
        converter.updateModifiedAndRev(node, entity);
      }
    }, node, entity);
  }

  public List<NodeConverter<? super T>> getNodeConverters() {
    return nodeConverters;
  }

  private void executeAction(ActionExecutor<T> actionExecutor, Node node, T entity) throws ConversionException {
    for (NodeConverter<? super T> converter : nodeConverters) {
      actionExecutor.execute(converter, node, entity);
    }
  }

  private interface ActionExecutor<T> {
    void execute(NodeConverter<? super T> nodeConverterm, Node node, T entity) throws ConversionException;
  }

  @Override
  public T convertToEntity(Node propertyContainer) throws ConversionException, InstantiationException {
    throw new UnsupportedOperationException("Yet to be implemented");
  }

  @Override
  public String getPropertyName(String fieldName) {
    throw new UnsupportedOperationException("Yet to be implemented");
  }

  @Override
  public <U extends T> U convertToSubType(Class<U> type, Node node) {
    throw new UnsupportedOperationException("Yet to be implemented");
  }

}
