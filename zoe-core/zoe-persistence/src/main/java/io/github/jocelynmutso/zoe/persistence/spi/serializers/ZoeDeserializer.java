package io.github.jocelynmutso.zoe.persistence.spi.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityBody;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityType;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Release;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;

public class ZoeDeserializer implements PersistenceConfig.Deserializer {

  private ObjectMapper objectMapper;
  
  public ZoeDeserializer(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends EntityBody> Entity<T> fromString(EntityType entityType, String value) {
    try {
      switch(entityType) {
        case ARTICLE: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Article>>() {});  
        }
        case LINK: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Link>>() {});  
        }
        case LOCALE: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Locale>>() {});  
        }
        case PAGE: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Page>>() {});  
        }
        case RELEASE: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Release>>() {});  
        }
        case WORKFLOW: {
          return (Entity<T>) objectMapper.readValue(value, new TypeReference<Entity<Workflow>>() {});  
        }
        default: throw new RuntimeException("can't map: " + entityType);
      }
      
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Entity<?> fromString(String value) {
    try {
      JsonNode node = objectMapper.readValue(value, JsonNode.class);
      final EntityType type = EntityType.valueOf(node.get("type").textValue());

      switch (type) {
      case ARTICLE: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Article>>() {});
      }
      case LINK: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Link>>() {});
      }
      case LOCALE: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Locale>>() {});
      }
      case PAGE: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Page>>() {});
      }
      case RELEASE: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Release>>() {});
      }
      case WORKFLOW: {
        return objectMapper.convertValue(node, new TypeReference<Entity<Workflow>>() {});
      }
      default:
        throw new RuntimeException("can't map: " + node);
      }

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage() + System.lineSeparator() + value, e);
    }
  }
}
