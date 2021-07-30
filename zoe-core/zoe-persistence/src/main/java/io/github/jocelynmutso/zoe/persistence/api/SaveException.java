package io.github.jocelynmutso.zoe.persistence.api;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.resys.thena.docdb.api.actions.CommitActions.CommitResult;

public class SaveException extends RuntimeException {
  private static final long serialVersionUID = 7190168525508589141L;
  
  private final Entity<?> entity;
  private final CommitResult commit;
  
  public SaveException(Entity<?> entity, CommitResult commit) {
    super(msg(entity, commit));
    this.entity = entity;
    this.commit = commit;
  }
  
  public Entity<?> getEntity() {
    return entity;
  }
  public CommitResult getCommit() {
    return commit;
  }
  
  private static String msg(Entity<?> entity, CommitResult commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit.getMessages()) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    
    return new StringBuilder("Can't save entity: ").append(entity.getType())
        .append(", because of: ").append(messages)
        .toString();
  }
}
