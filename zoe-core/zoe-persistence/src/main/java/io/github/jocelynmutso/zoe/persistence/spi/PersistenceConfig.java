package io.github.jocelynmutso.zoe.persistence.spi;

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

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityBody;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.resys.thena.docdb.api.DocDB;
import io.resys.thena.docdb.api.actions.ObjectsActions.BlobObjects;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsResult;
import io.smallrye.mutiny.Uni;

@Value.Immutable
public interface PersistenceConfig {
  DocDB getClient();
  String getRepoName();
  String getHeadName();
  AuthorProvider getAuthorProvider();
  
  Serializer getSerializer();
  Deserializer getDeserializer();
  
  GidProvider getGidProvider();
  
  @Value.Immutable
  interface EntityState<T extends EntityBody> {
    ObjectsResult<BlobObjects> getSrc();
    Entity<T> getEntity();
  }
  
  interface Commands {
    <T extends EntityBody> Uni<Entity<T>> delete(Entity<T> toBeDeleted);
    <T extends EntityBody> Uni<EntityState<T>> get(String blobId, EntityType type);
    <T extends EntityBody> Uni<Entity<T>> save(Entity<T> toBeSaved);
  }  
  
  @FunctionalInterface
  interface GidProvider {
    String getNextId(EntityType entity);
  }
  
  @FunctionalInterface
  interface AuthorProvider {
    String getAuthor();
  }
  
  @FunctionalInterface
  interface Serializer {
    String toString(Entity<?> entity);
  }
  
  interface Deserializer {
    Entity<?> fromString(String value);
    <T extends EntityBody> Entity<T> fromString(EntityType type, String value);
  }
  
}
