package nl.knaw.huygens.timbuctoo.index.request;

import nl.knaw.huygens.timbuctoo.index.IndexException;
import nl.knaw.huygens.timbuctoo.index.Indexer;
import nl.knaw.huygens.timbuctoo.messages.Action;
import nl.knaw.huygens.timbuctoo.messages.ActionType;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;

public interface IndexRequest {
  Class<? extends DomainEntity> getType();

  void execute(Indexer indexer) throws IndexException;

  Action toAction();

  ActionType getActionType();
}