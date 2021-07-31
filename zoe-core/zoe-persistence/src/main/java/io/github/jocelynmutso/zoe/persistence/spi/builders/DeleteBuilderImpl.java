package io.github.jocelynmutso.zoe.persistence.spi.builders;

import java.util.stream.Collectors;

import io.github.jocelynmutso.zoe.persistence.api.DeleteBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflow;
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

public class DeleteBuilderImpl extends PersistenceCommands implements DeleteBuilder {
  
  public DeleteBuilderImpl(PersistenceConfig config) {
    super(config);
  }

  @Override
  public Uni<Entity<Article>> article(String articleId) {
    // Delete the article
    return new ArticleDeleteVisitor(config, articleId).visit();
  
  }
  @Override
  public Uni<Entity<Locale>> locale(String localeId) {
    // Get the locale
    final Uni<EntityState<Locale>> query = get(localeId, EntityType.LOCALE);
    
    // Delete the locale
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  @Override
  public Uni<Entity<Page>> page(String pageId) {
    // Get the page
    final Uni<EntityState<Page>> query = get(pageId, EntityType.PAGE);
    
    // Delete the page
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  @Override
  public Uni<Entity<Link>> link(String linkId) {
    // Get the link
    final Uni<EntityState<Link>> query = get(linkId, EntityType.LINK);
    
    // Delete the link
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }
  
  @Override
  public Uni<Entity<Workflow>> workflow(String workflowId) {
    // Get the workflow
    final Uni<EntityState<Workflow>> query = get(workflowId, EntityType.WORKFLOW);
    
    // Delete the workflow
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  @Override
  public Uni<Entity<Link>> linkArticlePage(LinkArticlePage linkArticlePage) {
    
    // Get the link
    final Uni<EntityState<Link>> query = get(linkArticlePage.getLinkId(), EntityType.LINK);
    
    return query.onItem().transformToUni(state -> {
      final Entity<Link> start = state.getEntity();
      
      var newArticles = start.getBody()
          .getArticles().stream().filter(a -> !a.equals(linkArticlePage.getArticleId()))
          .collect(Collectors.toList());
      
      if(newArticles.size() == start.getBody().getArticles().size()) {
        return Uni.createFrom().item(start);
      }
      
      Entity<Link> end = ImmutableEntity.<Link>builder()
        .id(start.getId()).type(start.getType())
        .body(ImmutableLink.builder().from(start.getBody())
            .articles(newArticles)
            .build())
        .build();
      
      return save(end);
    });
  }

  @Override
  public Uni<Entity<Workflow>> workflowArticlePage(WorkflowArticlePage workflowArticlePage) {
    // Get the workflow
    final Uni<EntityState<Workflow>> query = get(workflowArticlePage.getWorkflowId(), EntityType.WORKFLOW);
    
    return query.onItem().transformToUni(state -> {
      final Entity<Workflow> start = state.getEntity();
      
      var newArticles = start.getBody()
          .getArticles().stream().filter(a -> !a.equals(workflowArticlePage.getArticleId()))
          .collect(Collectors.toList());
      
      if(newArticles.size() == start.getBody().getArticles().size()) {
        return Uni.createFrom().item(start);
      }
      
      Entity<Workflow> end = ImmutableEntity.<Workflow>builder()
        .id(start.getId()).type(start.getType())
        .body(ImmutableWorkflow.builder().from(start.getBody())
            .articles(newArticles)
            .build())
        .build();
      return save(end);
    });
  }
}
