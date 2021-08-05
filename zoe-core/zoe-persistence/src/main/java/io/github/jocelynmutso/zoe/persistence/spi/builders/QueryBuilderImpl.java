package io.github.jocelynmutso.zoe.persistence.spi.builders;

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

import io.github.jocelynmutso.zoe.persistence.api.ImmutableSiteState;
import io.github.jocelynmutso.zoe.persistence.api.RefException;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.QueryBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteContentType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteState;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceCommands;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.smallrye.mutiny.Uni;

public class QueryBuilderImpl extends PersistenceCommands implements QueryBuilder {
  
  public QueryBuilderImpl(PersistenceConfig config) {
    super(config);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Uni<SiteState> get() {
    final var siteName = config.getRepoName() + ":" + config.getHeadName();
    
    return config.getClient().repo().query().id(config.getRepoName()).get().onItem()
      .transformToUni(repo -> {
        if(repo == null) {
         return Uni.createFrom().item(ImmutableSiteState.builder()
              .name(siteName)
              .contentType(SiteContentType.NOT_CREATED)
              .build()); 
        }
      
        return config.getClient()
            .objects().refState()
            .repo(config.getRepoName())
            .ref(config.getHeadName())
            .blobs(true)
            .build().onItem()
            .transform(state -> {
              if(state.getStatus() == ObjectsStatus.ERROR) {
                throw new RefException(siteName, state);
              }
              // Nothing present
              if(state.getObjects() == null) {
                return ImmutableSiteState.builder()
                    .name(siteName)
                    .contentType(SiteContentType.EMPTY)
                    .build();
              }
              
              final var builder = ImmutableSiteState.builder();
              final var tree = state.getObjects().getTree();
              for(final var treeValue : tree.getValues().values()) {
                final var blob = state.getObjects().getBlobs().get(treeValue.getBlob());
                final var entity = config.getDeserializer().fromString(blob.getValue());
                final var id = entity.getId();
                
                switch (entity.getType()) {
                case ARTICLE:
                  builder.putArticles(id, (Entity<Article>) entity);
                  break;
                case LINK:
                  builder.putLinks(id, (Entity<Link>) entity);
                  break;
                case LOCALE:
                  builder.putLocales(id, (Entity<Locale>) entity);
                  break;
                case PAGE:
                  builder.putPages(id, (Entity<Page>) entity);
                  break;
                case RELEASE:
                  builder.putReleases(id, (Entity<Release>) entity);
                  break;
                case WORKFLOW:
                  builder.putWorkflows(id, (Entity<Workflow>) entity);
                  break;
                default: throw new RuntimeException("Don't know how to convert entity: " + entity.toString() + "!");
                }
              }
              
              return builder.name(siteName).contentType(SiteContentType.OK).build();
            });
      });
  }
}
