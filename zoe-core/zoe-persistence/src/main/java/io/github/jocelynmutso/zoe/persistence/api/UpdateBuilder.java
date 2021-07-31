package io.github.jocelynmutso.zoe.persistence.api;

import java.util.Optional;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.smallrye.mutiny.Uni;

public interface UpdateBuilder {
  
  Uni<Entity<Article>> article(ArticleMutator changes);
  
  Entity<Locale> locale(LocaleMutator changes);
  Entity<Page> page(PageMutator changes);
  Entity<Link> link(LinkMutator changes);
  Entity<Workflow> workflow(WorkflowMutator changes);

   
  interface LocaleMutator {
    String getLocaleId(); 
    Boolean getEnabled();
  }
  
  interface ArticleMutator {
    String getArticleId();
    Optional<String> getParentId();
    String getName();
    Integer getOrder();
  }
  
  interface PageMutator {
    String getPageId();
    String getContent();
    String getLocale();
  }
  
  interface LinkMutator {
    String getLinkId();
    String getContent(); 
    String getLocale(); 
    String getDescription();
  }
  
  interface WorkflowMutator {
    String getWorkflowId(); 
    String getName(); 
    String getLocale(); 
    String getContent();
  }
}
