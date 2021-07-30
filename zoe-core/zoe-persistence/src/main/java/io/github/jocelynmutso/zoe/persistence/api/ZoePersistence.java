package io.github.jocelynmutso.zoe.persistence.api;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface ZoePersistence {
  
  CreateBuilder create();
  UpdateBuilder update();
  DeleteBuilder delete();
 
  @Value.Immutable
  @JsonSerialize(as = ImmutableEntity.class)
  @JsonDeserialize(as = ImmutableEntity.class)
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

  @Value.Immutable
  @JsonSerialize(as = ImmutableLink.class)
  @JsonDeserialize(as = ImmutableLink.class)
  interface Link extends EntityBody {
    List<String> getArticles();
    String getType();
    String getLocale();
    String getContent();
    String getDescription();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableArticle.class)
  @JsonDeserialize(as = ImmutableArticle.class)
  interface Article extends EntityBody {
    Optional<String> getParentId();
    String getName();
    Integer getOrder();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutablePage.class)
  @JsonDeserialize(as = ImmutablePage.class)
  interface Page extends EntityBody {
    String getArticle();
    String getLocale();
    String getContent();
    
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableLocale.class)
  @JsonDeserialize(as = ImmutableLocale.class)
  interface Locale extends EntityBody {
    String getValue();
   // Boolean getEnabled();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableWorkflow.class)
  @JsonDeserialize(as = ImmutableWorkflow.class)
  interface Workflow extends EntityBody {
    String getName();
    String getLocale();
    String getContent();
    List<String> getArticles();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableRelease.class)
  @JsonDeserialize(as = ImmutableRelease.class)
  interface Release extends EntityBody {
    String getName();
    String getNote();
  }
}
