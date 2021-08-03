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
    List<String> getArticles();
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
