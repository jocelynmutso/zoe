package io.github.jocelynmutso.zoe.persistence.spi.builders;

import io.github.jocelynmutso.zoe.persistence.api.CreateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;
import io.smallrye.mutiny.Uni;

public class CreateBuilderImpl implements CreateBuilder {

  private final PersistenceConfig config;
  
  public CreateBuilderImpl(PersistenceConfig config) {
    super();
    this.config = config;
  }
  

  @Override
  public Uni<Entity<Article>> article(CreateArticle init) {
    final var gid = gid(EntityType.ARTICLE);
    final var article = ImmutableArticle.builder()
        .name(init.getName())
        .parentId(init.getParentId())
        .order(init.getOrder().orElse(0))
        .build();
    final Entity<Article> entity = ImmutableEntity.<Article>builder()
        .id(gid)
        .type(EntityType.ARTICLE)
        .body(article)
        .build();
    
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-article")
      .parentIsLatest()
      .author(config.getAuthorProvider().getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        return entity;
      });
  }

  @Override
  public Entity<Release> release(CreateRelease init) {
    return null;
  }

  @Override
  public Entity<Locale> locale(CreateLocale init) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Page> page(CreatePage init) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Link> link(CreateLink init) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Workflow> workflow(CreateWorkflow init) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private String gid(EntityType type) {
    return config.getGidProvider().getNextId(type);
  }

}
