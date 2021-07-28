package io.github.jocelynmutso.zoe.persistence;

import java.util.Optional;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Workflow;

public interface CreateBuilder {
  Entity<Release> release(CreateRelease init);
  Entity<Locale> locale(CreateLocale init);
  Entity<Article> article(CreateArticle init);
  Entity<Page> page(CreatePage init);
  Entity<Link> link(CreateLink init);
  Entity<Workflow> workflow(CreateWorkflow init);

  
  @Value.Immutable
  interface CreateRelease {
    String getName();
    Optional<String> getNote();
  }
  
  @Value.Immutable
  interface CreateLocale {
    String getLocale();
  }
  
  @Value.Immutable
  interface CreateArticle {
    Optional<String> getParentId();
    String getName();
    Integer getOrder(); 
  }
  
  @Value.Immutable
  interface CreatePage {
    String getArticleId();
    String getLocale();
    Optional<String> getContent();
  }
  
  @Value.Immutable
  interface CreateLink {
    String getValue();
    String getLocale();
    String getDescription(); 
    String getType();
  }
  
  @Value.Immutable
  interface CreateWorkflow {
    String getName();
    String getLocale(); 
    String getContent(); 
  }
}