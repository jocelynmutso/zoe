package io.github.jocelynmutso.zoe.persistence.api;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.resys.thena.docdb.api.actions.ObjectsActions.BlobObjects;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;

public class DeleteException extends RuntimeException {
  private static final long serialVersionUID = 7190168525508589141L;
  
  private final String entityId;
  private final EntityType type;
  private final ObjectsResult<BlobObjects> commit;
  
  public DeleteException(String entityId, EntityType type, ObjectsResult<BlobObjects> commit) {
    super(msg(entityId, type, commit));
    this.entityId = entityId;
    this.type = type;
    this.commit = commit;
  }
  public String getEntityId() {
    return entityId;
  }
  public EntityType getType() {
    return type;
  }
  public ObjectsResult<BlobObjects> getCommit() {
    return commit;
  }
  private static String msg(String entityId, EntityType type, ObjectsResult<BlobObjects> commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit.getMessages()) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    
    return new StringBuilder("Can't delete entity: ").append(type)
        .append(", id: ").append(entityId)
        .append(", because of: ").append(messages)
        .toString();
  }
}
