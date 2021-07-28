package io.github.jocelynmutso.zoe.persistence.test;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateArticle;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.test.config.MongoDbConfig;


public class PersistenceTest extends MongoDbConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceTest.class);
  
  
  @Test
  public void test1() {
    final var repo = getPersistence("test1");
    
    Entity<Article> article1 = repo.create().article(
        ImmutableCreateArticle.builder().name("My first article").order(100).build()
    )
    .await().atMost(Duration.ofMinutes(1));

    Entity<Article> article2 = repo.create().article(
        ImmutableCreateArticle.builder().name("My second article").order(100).build()
    )
    .await().atMost(Duration.ofMinutes(1));
    
    
    super.prettyPrint("test1");
  }

  
  
}
