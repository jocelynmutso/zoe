package io.github.jocelynmutso.zoe.persistence.api;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.smallrye.mutiny.Uni;

public interface UpdateBuilder {
  
  Uni<Entity<Article>> article(ArticleMutator changes);
  Uni<Entity<Locale>> locale(LocaleMutator changes);
  Uni<Entity<Page>> page(PageMutator changes);
  Uni<Entity<Link>> link(LinkMutator changes);
  Uni<Entity<Workflow>> workflow(WorkflowMutator changes);

  @Value.Immutable
  interface LocaleMutator {
    String getLocaleId(); 
    String getValue();
    Boolean getEnabled();
  }
  
  @Value.Immutable
  interface ArticleMutator {
    String getArticleId();
    Optional<String> getParentId();
    String getName();
    Integer getOrder();
  }
  @Value.Immutable
  interface PageMutator {
    String getPageId();
    String getContent();
    String getLocale();
  }
  @Value.Immutable
  interface LinkMutator {
    String getLinkId();
    String getContent(); 
    String getLocale(); 
    String getDescription();
    String getType();
  }
  @Value.Immutable
  interface WorkflowMutator {
    String getWorkflowId(); 
    String getName(); 
    String getLocale(); 
    String getContent();
    List<String> getArticles();
  }
}
