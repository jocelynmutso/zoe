package io.github.jocelynmutso.zoe.persistence.spi.builders;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflow;
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
  public Uni<Entity<Locale>> locale(LocaleMutator changes) {
    // Get the locale
    final Uni<EntityState<Locale>> query = get(changes.getLocaleId(), EntityType.LOCALE);
    
    // Change the locale
    return query.onItem().transformToUni(state -> save(changeLocale(state, changes)));
  }
  
  private Entity<Locale> changeLocale(EntityState<Locale> state, LocaleMutator changes) {
    final var start = state.getEntity();
    return ImmutableEntity.<Locale>builder()
        .from(start)
        .body(ImmutableLocale.builder().from(start.getBody())
            .value(changes.getValue())
            .build())
        .build();
  }

  @Override
  public Uni<Entity<Page>> page(PageMutator changes) {
    // Get the page
    final Uni<EntityState<Page>> query = get(changes.getPageId(), EntityType.PAGE);
    
    // Change the page
    return query.onItem().transformToUni(state -> save(changePage(state, changes)));
  }
  
  private Entity<Page> changePage(EntityState<Page> state, PageMutator changes) {
    final var start = state.getEntity();
    return ImmutableEntity.<Page>builder()
        .from(start)
        .body(ImmutablePage.builder().from(start.getBody())
            .content(changes.getContent())
            .locale(changes.getLocale())
            .build())
        .build();
  }

  @Override
  public Uni<Entity<Link>> link(LinkMutator changes) {
    // Get the link
    final Uni<EntityState<Link>> query = get(changes.getLinkId(), EntityType.LINK);
    
    // Change the link
    return query.onItem().transformToUni(state -> save(changeLink(state, changes)));
  }
  
  private Entity<Link> changeLink(EntityState<Link> state, LinkMutator changes) {
    final var start = state.getEntity();
    return ImmutableEntity.<Link>builder()
        .from(start)
        .body(ImmutableLink.builder().from(start.getBody())
            .content(changes.getContent())
            .description(changes.getDescription())
            .locale(changes.getLocale())
            .type(changes.getType())
            .articles(changes.getArticles())
            .build())
        .build();
  }

  @Override
  public Uni<Entity<Workflow>> workflow(WorkflowMutator changes) {
    // Get the Workflow
    final Uni<EntityState<Workflow>> query = get(changes.getWorkflowId(), EntityType.WORKFLOW);
    
    // Change the Workflow
    return query.onItem().transformToUni(state -> save(changeWorkflow(state, changes)));
  }
  
  private Entity<Workflow> changeWorkflow(EntityState<Workflow> state, WorkflowMutator changes) {
    final var start = state.getEntity();
    return ImmutableEntity.<Workflow>builder()
        .from(start)
        .body(ImmutableWorkflow.builder().from(start.getBody())
            .content(changes.getContent())
            .locale(changes.getLocale())
            .name(changes.getName())
            .articles(changes.getArticles())
            .build())
        .build();
  }
}
