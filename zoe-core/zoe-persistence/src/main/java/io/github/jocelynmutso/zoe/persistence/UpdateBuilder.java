package io.github.jocelynmutso.zoe.persistence;

import java.util.Optional;

import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.ZoePersistence.Workflow;

public interface UpdateBuilder {
  
  Entity<Locale> locale(LocaleMutator changes);
  Entity<Article> article(ArticleMutator changes);
  Entity<Page> page(PageMutator changes);
  Entity<Link> link(LinkMutator changes);
  Entity<Workflow> workflow(WorkflowMutator changes);
  
  
  /*
    workflow(workflow: WorkflowMutator): Promise<Workflow>;
   */
   
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
    String getLocalisedContent();
    String getLocale();
  }
  
  interface LinkMutator {
    String getLinkId();
    String getLocalisedContent(); 
    String getLocale(); 
    String getDescription();
  }
  
  interface WorkflowMutator {
    String getWorkflowId(); 
    String getName(); 
    String getLocale(); 
    String getLocalisedContent();
  }
}
