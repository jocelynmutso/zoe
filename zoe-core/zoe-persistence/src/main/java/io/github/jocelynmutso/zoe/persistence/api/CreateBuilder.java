package io.github.jocelynmutso.zoe.persistence.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityRepo;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.smallrye.mutiny.Uni;

public interface CreateBuilder {
  
  Uni<EntityRepo> repo();
  Uni<Entity<Article>> article(CreateArticle init);
  Uni<Entity<Release>> release(CreateRelease init);
  Uni<Entity<Locale>> locale(CreateLocale init);
  Uni<Entity<Page>> page(CreatePage init);
  Uni<Entity<Link>> link(CreateLink init);
  Uni<Entity<Workflow>> workflow(CreateWorkflow init);
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateArticle.class)
  @JsonDeserialize(as = ImmutableCreateArticle.class)
  interface CreateArticle {
    @Nullable
    String getParentId();
    String getName();
    @Nullable
    Integer getOrder(); 
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateRelease.class)
  @JsonDeserialize(as = ImmutableCreateRelease.class)
  interface CreateRelease {
    String getName();
    @Nullable
    String getNote();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateLocale.class)
  @JsonDeserialize(as = ImmutableCreateLocale.class)
  interface CreateLocale {
    String getLocale();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreatePage.class)
  @JsonDeserialize(as = ImmutableCreatePage.class)
  interface CreatePage {
    String getArticleId();
    String getLocale();
    @Nullable
    String getContent();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateLink.class)
  @JsonDeserialize(as = ImmutableCreateLink.class)
  interface CreateLink {
    String getValue();
    String getLocale();
    String getDescription(); 
    String getType();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableCreateWorkflow.class)
  @JsonDeserialize(as = ImmutableCreateWorkflow.class)
  interface CreateWorkflow {
    String getName();
    String getLocale(); 
    String getContent(); 
  }
}
