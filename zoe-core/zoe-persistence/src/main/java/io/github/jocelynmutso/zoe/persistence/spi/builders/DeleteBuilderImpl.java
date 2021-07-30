package io.github.jocelynmutso.zoe.persistence.spi.builders;

import com.sun.source.doctree.EntityTree;

import io.github.jocelynmutso.zoe.persistence.api.SaveException;
import io.github.jocelynmutso.zoe.persistence.api.DeleteBuilder;
import io.github.jocelynmutso.zoe.persistence.api.DeleteException;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.smallrye.mutiny.Uni;

public class DeleteBuilderImpl implements DeleteBuilder {
  private final PersistenceConfig config;
  
  public DeleteBuilderImpl(PersistenceConfig config) {
    super();
    this.config = config;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Article>> article(String articleId) {
    return config.getClient()
    .objects().blobState()
    .repo(config.getRepoName())
    .anyId(config.getHeadName())
    .blobName(articleId)
    .build()
    .onItem().transformToUni(state -> {
      if(state.getStatus() == ObjectsStatus.OK) {
        Entity<Article> entity = (Entity<Article>) config.getDeserializer()
            .fromString(EntityType.ARTICLE, state.getObjects().getBlob().getValue());
        
        return config.getClient().commit().head()
            .head(config.getRepoName(), config.getHeadName())
            .message("delete-article: " + articleId)
            .parentIsLatest()
            .author(config.getAuthorProvider().getAuthor())
            .remove(articleId)
            .build().onItem().transform(commit -> {
              if(commit.getStatus() == CommitStatus.OK) {
                return entity;
              }
              throw new SaveException(entity, commit);
            });
      }
      throw new DeleteException(articleId, EntityType.ARTICLE, state);
    });
  }
  
  @Override
  public Uni<Entity<Locale>> locale(String localeId) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Uni<Entity<Page>> page(String PageId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Entity<Link>> link(String LinkId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Entity<Link>> linkArticlePage(LinkArticlePage linkArticlePage) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Entity<Workflow>> workflow(String WorkflowId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Entity<Workflow>> workflowArticlePage(WorkflowArticlePage workflowArticlePage) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
