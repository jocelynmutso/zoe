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
  
  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Locale>> locale(String localeId) {
    return config.getClient()
    .objects().blobState()
    .repo(config.getRepoName())
    .anyId(config.getHeadName())
    .blobName(localeId)
    .build()
    .onItem().transformToUni(state -> {
      if(state.getStatus() == ObjectsStatus.OK) {
        Entity<Locale> entity = (Entity<Locale>) config.getDeserializer()
            .fromString(EntityType.LOCALE, state.getObjects().getBlob().getValue());
        
        return config.getClient().commit().head()
            .head(config.getRepoName(), config.getHeadName())
            .message("delete-locale: " + localeId)
            .parentIsLatest()
            .author(config.getAuthorProvider().getAuthor())
            .remove(localeId)
            .build().onItem().transform(commit -> {
              if(commit.getStatus() == CommitStatus.OK) {
                return entity;
              }
              throw new SaveException(entity, commit);
            });
      }
      throw new DeleteException(localeId, EntityType.LOCALE, state);
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Page>> page(String PageId) {
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(config.getHeadName())
        .blobName(PageId)
        .build()
        .onItem().transformToUni(state -> {
          if(state.getStatus() == ObjectsStatus.OK) {
            Entity<Page> entity = (Entity<Page>) config.getDeserializer()
                .fromString(EntityType.PAGE, state.getObjects().getBlob().getValue());
            
            return config.getClient().commit().head()
                .head(config.getRepoName(), config.getHeadName())
                .message("delete-page: " + PageId)
                .parentIsLatest()
                .author(config.getAuthorProvider().getAuthor())
                .remove(PageId)
                .build().onItem().transform(commit -> {
                  if(commit.getStatus() == CommitStatus.OK) {
                    return entity;
                  }
                  throw new SaveException(entity, commit);
                });
          }
          throw new DeleteException(PageId, EntityType.PAGE, state);
        });
  }

  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Link>> link(String LinkId) {
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(config.getHeadName())
        .blobName(LinkId)
        .build()
        .onItem().transformToUni(state -> {
          if(state.getStatus() == ObjectsStatus.OK) {
            Entity<Link> entity = (Entity<Link>) config.getDeserializer()
                .fromString(EntityType.LINK, state.getObjects().getBlob().getValue());
            
            return config.getClient().commit().head()
                .head(config.getRepoName(), config.getHeadName())
                .message("delete-link: " + LinkId)
                .parentIsLatest()
                .author(config.getAuthorProvider().getAuthor())
                .remove(LinkId)
                .build().onItem().transform(commit -> {
                  if(commit.getStatus() == CommitStatus.OK) {
                    return entity;
                  }
                  throw new SaveException(entity, commit);
                });
          }
          throw new DeleteException(LinkId, EntityType.LINK, state);
        });
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Workflow>> workflow(String WorkflowId) {
    return config.getClient()
        .objects().blobState()
        .repo(config.getRepoName())
        .anyId(config.getHeadName())
        .blobName(WorkflowId)
        .build()
        .onItem().transformToUni(state -> {
          if(state.getStatus() == ObjectsStatus.OK) {
            Entity<Workflow> entity = (Entity<Workflow>) config.getDeserializer()
                .fromString(EntityType.WORKFLOW, state.getObjects().getBlob().getValue());
            
            return config.getClient().commit().head()
                .head(config.getRepoName(), config.getHeadName())
                .message("delete-link: " + WorkflowId)
                .parentIsLatest()
                .author(config.getAuthorProvider().getAuthor())
                .remove(WorkflowId)
                .build().onItem().transform(commit -> {
                  if(commit.getStatus() == CommitStatus.OK) {
                    return entity;
                  }
                  throw new SaveException(entity, commit);
                });
          }
          throw new DeleteException(WorkflowId, EntityType.WORKFLOW, state);
        });
  }

  @SuppressWarnings("unchecked")
  @Override
  public Uni<Entity<Link>> linkArticlePage(LinkArticlePage linkArticlePage) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Entity<Workflow>> workflowArticlePage(WorkflowArticlePage workflowArticlePage) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
