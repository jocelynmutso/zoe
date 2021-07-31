package io.github.jocelynmutso.zoe.persistence.api;

import java.util.List;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.resys.thena.docdb.api.models.Message;

public class QueryException extends RuntimeException {
  private static final long serialVersionUID = 7190168525508589141L;
  
  private final String entityId;
  private final EntityType type;
  private final List<Message> commit;
  
  public QueryException(String entityId, EntityType type, ObjectsResult<?> commit) {
    super(msg(entityId, type, commit.getMessages()));
    this.entityId = entityId;
    this.type = type;
    this.commit = commit.getMessages();
  }
  public String getEntityId() {
    return entityId;
  }
  public EntityType getType() {
    return type;
  }
  public List<Message> getCommit() {
    return commit;
  }
  private static String msg(String entityId, EntityType type, List<Message> commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    
    return new StringBuilder("Can't find entity: ").append(type)
        .append(", id: ").append(entityId)
        .append(", because of: ").append(messages)
        .toString();
  }
}
