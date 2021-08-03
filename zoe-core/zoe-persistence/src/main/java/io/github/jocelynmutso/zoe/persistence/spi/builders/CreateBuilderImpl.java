package io.github.jocelynmutso.zoe.persistence.spi.builders;

import java.util.Optional;

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

import io.github.jocelynmutso.zoe.persistence.api.CreateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableRelease;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflow;
import io.github.jocelynmutso.zoe.persistence.api.SaveException;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.smallrye.mutiny.Uni;


public class CreateBuilderImpl implements CreateBuilder {
  
  private final PersistenceConfig config;
  
  public CreateBuilderImpl(PersistenceConfig config) {
    super();
    this.config = config;
  }
  
  @Override
  public Uni<Entity<Article>> article(CreateArticle init) {
    final var gid = gid(EntityType.ARTICLE);
    final var article = ImmutableArticle.builder()
        .name(init.getName())
        .parentId(init.getParentId())
        .order(Optional.ofNullable(init.getOrder()).orElse(0))
        .build();
    final Entity<Article> entity = ImmutableEntity.<Article>builder()
        .id(gid)
        .type(EntityType.ARTICLE)
        .body(article)
        .build();
    
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-article")
      .parentIsLatest()
      .author(config.getAuthorProvider().getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        throw new SaveException(entity, commit);
      });
  }

  @Override
  public Uni<Entity<Release>> release(CreateRelease init) {
    final var gid = gid(EntityType.RELEASE);
    
    final var release = ImmutableRelease.builder()
        .name(init.getName())
        .note(Optional.ofNullable(init.getNote()).orElse(""))
        .build();

    final Entity<Release> entity = ImmutableEntity.<Release>builder()
        .id(gid)
        .type(EntityType.RELEASE)
        .body(release)
        .build();
        
    return config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message("creating-release")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor())
        .append(gid, config.getSerializer().toString(entity))
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return entity;
          }
          throw new SaveException(entity, commit);
        });
  }

  @Override
  public Uni<Entity<Locale>> locale(CreateLocale init) {
    final var gid = gid(EntityType.LOCALE);
    final var locale = ImmutableLocale.builder()
        .value(init.getLocale())
        .build();
    
    final Entity<Locale> entity = ImmutableEntity.<Locale>builder()
        .id(gid)
        .type(EntityType.LOCALE)
        .body(locale)
        .build();
    
    return config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message("creating-locale")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor())
        .append(gid, config.getSerializer().toString(entity))
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return entity;
          }
          throw new SaveException(entity, commit);
        });
  }

  @Override
  public Uni<Entity<Page>> page(CreatePage init) {
    final var gid = gid(EntityType.PAGE);
    final var page = ImmutablePage.builder()
        .article(init.getArticleId())
        .locale(init.getLocale())
        .content(Optional.ofNullable(init.getContent()).orElse(""))
        .build();
    
    final Entity<Page> entity = ImmutableEntity.<Page>builder()
        .id(gid)
        .type(EntityType.PAGE)
        .body(page)
        .build();
    
    return config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message("creating-page")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor())
        .append(gid, config.getSerializer().toString(entity))
        .build().onItem().transform(commit -> {
          if(commit.getStatus() == CommitStatus.OK) {
            return entity;
          }
          throw new SaveException(entity, commit);
        });
  }

  @Override
  public Uni<Entity<Link>> link(CreateLink init) {
    final var gid = gid(EntityType.LINK);
    final var link = ImmutableLink.builder()
      .description(init.getDescription())
      .locale(init.getLocale())
      .type(init.getType())
      .content(init.getValue())
      .build();
    
    final Entity<Link> entity = ImmutableEntity.<Link>builder()
      .id(gid)
      .type(EntityType.LINK)
      .body(link)
      .build();
  
    return config.getClient().commit().head()
      .head(config.getRepoName(), config.getHeadName())
      .message("creating-link")
      .parentIsLatest()
      .author(config.getAuthorProvider().getAuthor())
      .append(gid, config.getSerializer().toString(entity))
      .build().onItem().transform(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return entity;
        }
        throw new SaveException(entity, commit);
      });
  }

  @Override
  public Uni<Entity<Workflow>> workflow(CreateWorkflow init) {
      final var gid = gid(EntityType.WORKFLOW);
      final var workflow = ImmutableWorkflow.builder()
        .name(init.getName())
        .locale(init.getLocale())
        .content(init.getContent())
        .build();
      
      final Entity<Workflow> entity = ImmutableEntity.<Workflow>builder()
          .id(gid)
          .type(EntityType.WORKFLOW)
          .body(workflow)
          .build();
      
      return config.getClient().commit().head()
          .head(config.getRepoName(), config.getHeadName())
          .message("creating-workflow")
          .parentIsLatest()
          .author(config.getAuthorProvider().getAuthor())
          .append(gid, config.getSerializer().toString(entity))
          .build().onItem().transform(commit -> {
            if(commit.getStatus() == CommitStatus.OK) {
              return entity;
            }
            throw new SaveException(entity, commit);
          });
  }
  
  private String gid(EntityType type) {
    return config.getGidProvider().getNextId(type);
  }
}
