package io.github.jocelynmutso.zoe.persistence.spi.builders;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.UpdateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceCommands;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig.EntityState;
import io.smallrye.mutiny.Uni;

public class UpdateBuilderImpl extends PersistenceCommands implements UpdateBuilder {

  public UpdateBuilderImpl(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<Entity<Article>> article(ArticleMutator changes) {
    // Get the article
    final Uni<EntityState<Article>> query = get(changes.getArticleId(), EntityType.ARTICLE);
    
    // Change the article
    return query.onItem().transformToUni(state -> save(changeArticle(state, changes)));
  }
  
  private Entity<Article> changeArticle(EntityState<Article> state, ArticleMutator changes) {
    final var start = state.getEntity();
    return ImmutableEntity.<Article>builder()
        .from(start)
        .body(ImmutableArticle.builder().from(start.getBody())
            .name(changes.getName())
            .order(changes.getOrder())
            .parentId(changes.getParentId())
            .build())
        .build();
  }
  
  @Override
  public Entity<Locale> locale(LocaleMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Page> page(PageMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Link> link(LinkMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Workflow> workflow(WorkflowMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }
}
