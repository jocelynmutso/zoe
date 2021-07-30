package io.github.jocelynmutso.zoe.persistence.spi;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityBody;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.resys.thena.docdb.api.DocDB;

@Value.Immutable
public interface PersistenceConfig {
  DocDB getClient();
  String getRepoName();
  String getHeadName();
  AuthorProvider getAuthorProvider();
  Serializer getSerializer();
  Deserializer getDeserializer();
  GidProvider getGidProvider();
  
  @FunctionalInterface
  interface GidProvider {
    String getNextId(EntityType entity);
  }
  
  @FunctionalInterface
  interface AuthorProvider {
    String getAuthor();
  }
  
  @FunctionalInterface
  interface Deserializer {
    Entity<?> fromString(EntityType type, String value);
  }
  
  @FunctionalInterface
  interface Serializer {
    String toString(Entity<?> entity);
  }
}
