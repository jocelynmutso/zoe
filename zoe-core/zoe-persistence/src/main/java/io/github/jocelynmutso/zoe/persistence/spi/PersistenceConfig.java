package io.github.jocelynmutso.zoe.persistence.spi;

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
