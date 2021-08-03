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

import java.util.Optional;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.smallrye.mutiny.Uni;

public interface CreateBuilder {
  
  Uni<Entity<Article>> article(CreateArticle init);
  Uni<Entity<Release>> release(CreateRelease init);
  Uni<Entity<Locale>> locale(CreateLocale init);
  Uni<Entity<Page>> page(CreatePage init);
  Uni<Entity<Link>> link(CreateLink init);
  Uni<Entity<Workflow>> workflow(CreateWorkflow init);

  
  @Value.Immutable
  interface CreateArticle {
    Optional<String> getParentId();
    String getName();
    Optional<Integer> getOrder(); 
  }
  
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
