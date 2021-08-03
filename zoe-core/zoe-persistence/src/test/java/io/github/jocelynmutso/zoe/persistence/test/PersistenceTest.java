package io.github.jocelynmutso.zoe.persistence.test;

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

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreatePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateRelease;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateWorkflow;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLinkMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLocaleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePageMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflowMutator;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.test.config.MongoDbConfig;


public class PersistenceTest extends MongoDbConfig {

  
  
  @Test
  public void test1() {
    final var repo = getPersistence("test1");
    
   Entity<Article> article1 = repo.create().article(
        ImmutableCreateArticle.builder().name("My first article").order(100).build()
    ).await().atMost(Duration.ofMinutes(1));

   Entity<Article> article2 = repo.create().article(
        ImmutableCreateArticle.builder().name("My second article").order(100).build()
    ).await().atMost(Duration.ofMinutes(1));
    
    repo.create().release(
        ImmutableCreateRelease.builder().name("v1.5").note("test release").build()
     ).await().atMost(Duration.ofMinutes(1));
    
    repo.create().release(
        ImmutableCreateRelease.builder().name("v2.4").note("new content").build()
     ).await().atMost(Duration.ofMinutes(1));
    
    Entity<Locale> locale1 = repo.create().locale(
        ImmutableCreateLocale.builder().locale("en").build()
      ).await().atMost(Duration.ofMinutes(1));
    
    Entity<Page> page1 = repo.create().page(
        ImmutableCreatePage.builder().articleId("A1").locale("en").content("# English content").build()
      ).await().atMost(Duration.ofMinutes(1));
    
    repo.create().page(
        ImmutableCreatePage.builder().articleId("A1").locale("fi").content("# Finnish content").build()
      ).await().atMost(Duration.ofMinutes(1));
    
    Entity<Link> link1 = repo.create().link(
        ImmutableCreateLink.builder().type("internal").locale("en").description("click me").value("www.example.com").build()
      ).await().atMost(Duration.ofMinutes(1));
    
    Entity<Workflow> workflow1 = repo.create().workflow( 
        ImmutableCreateWorkflow.builder().name("Form1").locale("en").content("firstForm").build()
      ).await().atMost(Duration.ofMinutes(1));
    
    // create state
    var expected = super.toString(getClass(), "create_state.txt");
    var actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
    repo.update().article(ImmutableArticleMutator.builder().articleId(article1.getId()).name("Revised Article1").order(300).build())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.update().locale(ImmutableLocaleMutator.builder().localeId(locale1.getId()).value("fi").enabled(false).build())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.update().page(ImmutablePageMutator.builder().pageId(page1.getId()).content("new content for page1").locale("fi").build())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.update().link(ImmutableLinkMutator.builder().linkId(link1.getId()).articles(Arrays.asList("A1")).description("Don't click me").locale("sv").content("www.wikipedia.com").type("external").build())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.update().workflow(ImmutableWorkflowMutator.builder().workflowId(workflow1.getId()).content("revision of firstForm").locale("sv").name("First form part 2").build())
    .await().atMost(Duration.ofMinutes(1));
    
    
    // update state
    expected = super.toString(getClass(), "update_state.txt");
    actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
    
    repo.delete().article(article1.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.delete().article(article2.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.delete().locale(locale1.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.delete().page(page1.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.delete().link(link1.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    repo.delete().workflow(workflow1.getId())
    .await().atMost(Duration.ofMinutes(1));
    
    // delete state
    expected = super.toString(getClass(), "delete_state.txt");
    actual = super.toRepoExport("test1");
    Assertions.assertEquals(expected, actual);
    
  }

}
