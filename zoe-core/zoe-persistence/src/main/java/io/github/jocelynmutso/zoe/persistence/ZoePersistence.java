package io.github.jocelynmutso.zoe.persistence;

import java.io.Serializable;
import java.util.List;

public interface ZoePersistence {

  interface Entity<T extends EntityBody> extends Serializable {
    String getId();
    EntityType getType();
    T getBody();
  }

  enum EntityType {
    LOCALE, LINK, ARTICLE, WORKFLOW, RELEASE, PAGE
  }

  interface EntityBody extends Serializable {
  }

  interface Link extends EntityBody {
    List<String> getArticles();
    String getType();
    String getLocale();
    String getContent();
    String getDescription();
  }
  
  interface Article extends EntityBody {
    String getParentId();
    String getName();
    Integer getOrder();
  }
  
  interface Page extends EntityBody {
    String getArticle();
    String getLocale();
    String getContent();
    
  }
  
  interface Locale extends EntityBody {
    String getValue();
    Boolean getEnabled();
  }
  
  interface Workflow extends EntityBody {
    String getName();
    String getLocale();
    String getContent();
    List<String> getArticles();
  }
  
  interface Release extends EntityBody {
    String getName();
    String getCreated();
    String getNote();
  }
}
