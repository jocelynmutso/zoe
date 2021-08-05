package io.github.jocelynmutso.zoe.persistence.api;

/*-
 * #%L
 * zoe-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.smallrye.mutiny.Uni;


public interface ZoePersistence {

  CreateBuilder create();
  UpdateBuilder update();
  DeleteBuilder delete();
  QueryBuilder query();
  
  interface QueryBuilder {
    Uni<SiteState> get();
  }

  
  @Value.Immutable
  @JsonSerialize(as = ImmutableSiteState.class)
  @JsonDeserialize(as = ImmutableSiteState.class)
  interface SiteState {
    String getName(); 
    SiteContentType getContentType();
    Map<String, Entity<Release>> getReleases();
    Map<String, Entity<Locale>> getLocales();
    Map<String, Entity<Page>> getPages();
    Map<String, Entity<Link>> getLinks();
    Map<String, Entity<Article>> getArticles();
    Map<String, Entity<Workflow>> getWorkflows();
  }
  
  enum SiteContentType {
    OK, ERRORS, NO_CREATED
  }
  
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
    String getLocale();
    String getContent();
    String getContentType();
    String getDescription();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableArticle.class)
  @JsonDeserialize(as = ImmutableArticle.class)
  interface Article extends EntityBody {
    @Nullable
    String getParentId();
    String getName();
    Integer getOrder();
  }
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableLocale.class)
  @JsonDeserialize(as = ImmutableLocale.class)
  interface Locale extends EntityBody {
    String getValue();
   // Boolean getEnabled();
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
