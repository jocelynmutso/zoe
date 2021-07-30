package io.github.jocelynmutso.zoe.persistence.api;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.smallrye.mutiny.Uni;

public interface DeleteBuilder {

  Uni<Entity<Locale>> locale(String localeId);
  Uni<Entity<Article>> article(String articleId);
  Uni<Entity<Page>> page(String pageId);
  Uni<Entity<Link>> link(String linkId);
  Uni<Entity<Link>> linkArticlePage(LinkArticlePage linkArticlePage);
  Uni<Entity<Workflow>> workflow(String workflowId);
  Uni<Entity<Workflow>> workflowArticlePage(WorkflowArticlePage workflowArticlePage);

  
  @Value.Immutable
  interface LinkArticlePage {
    String getLinkId(); 
    String getArticleId();
    String getLocale();
  }
  
  @Value.Immutable
  interface WorkflowArticlePage {
    String getWorkflowId();
    String getArticleId();
    String getLocale();
  }

}
