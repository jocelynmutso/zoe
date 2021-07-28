package io.github.jocelynmutso.zoe.persistence.api;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;

public interface DeleteBuilder {

  Entity<Locale> locale(String localeId);
  Entity<Article> article(String ArticleId);
  Entity<Page> page(String PageId);
  Entity<Link> link(String LinkId);
  Entity<Link> linkArticlePage(LinkArticlePage linkArticlePage);
  Entity<Workflow> workflow(String WorkflowId);
  Entity<Workflow> workflowArticlePage(WorkflowArticlePage workflowArticlePage);

  
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
