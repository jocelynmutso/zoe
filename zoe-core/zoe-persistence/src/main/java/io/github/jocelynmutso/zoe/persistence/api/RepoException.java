package io.github.jocelynmutso.zoe.persistence.api;

import io.resys.thena.docdb.api.actions.RepoActions.RepoResult;

public class RepoException extends RuntimeException {
  private static final long serialVersionUID = 7190168525508589141L;
  
  private final String entity;
  private final RepoResult commit;
  
  public RepoException(String entity, RepoResult commit) {
    super(msg(entity, commit));
    this.entity = entity;
    this.commit = commit;
  }
  
  public String getEntity() {
    return entity;
  }
  public RepoResult getCommit() {
    return commit;
  }
  
  private static String msg(String entity, RepoResult commit) {
    StringBuilder messages = new StringBuilder();
    for(var msg : commit.getMessages()) {
      messages
      .append(System.lineSeparator())
      .append("  - ").append(msg.getText());
    }
    
    return new StringBuilder("Error in repository: ").append(entity)
        .append(", because of: ").append(messages)
        .toString();
  }
}
