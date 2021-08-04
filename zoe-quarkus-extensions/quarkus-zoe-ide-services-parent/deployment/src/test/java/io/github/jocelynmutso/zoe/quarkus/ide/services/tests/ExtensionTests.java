package io.github.jocelynmutso.zoe.quarkus.ide.services.tests;

/*-
 * #%L
 * quarkus-zoe-ide-deployment
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreatePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateRelease;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateWorkflow;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLinkMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePageMutator;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.vertx.core.json.JsonObject;


//-Djava.util.logging.manager=org.jboss.logmanager.LogManager
public class ExtensionTests extends MongoDbConfig {
  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addAsResource(new StringAsset(
          "quarkus.zoe-ide-services.db.db-name=junit\r\n"+
          "quarkus.zoe-ide-services.db.connection-url=mongodb://localhost:12345\r\n" +
          "quarkus.zoe-ide-services.repo.repo-name=test-assets\r\n"
          ), "application.properties")
    );

  
  @Test
  public void postArticles() {

    RestAssured.given()
    .when().post("/zoe-ide-services")
    .then().statusCode(200);
    
   String articleId = RestAssured.given()
      .body(
          JsonObject.mapFrom(
            ImmutableCreateArticle.builder()
            .name("test-article")
            .build()
          ).toString())
      .when().post("/zoe-ide-services/articles")
      .then()
      .extract()
        .response()
        .body()
        .path("id");
  
   
    
   String pageId = RestAssured.given()
      .body(
          JsonObject.mapFrom(
              ImmutableCreatePage.builder()
              .locale("en")
              .content("# Header 1")
              .articleId("A1")
              .build()
              ).toString())
            .when().post("/zoe-ide-services/pages")
            .then()
              .extract()
              .response()
              .body()
              .path("id");
    
   String linkId = RestAssured.given()
    .body(
        JsonObject.mapFrom(
            ImmutableCreateLink.builder()
            .locale("en")
            .description("description")
            .value("www.example.com")
            .type("internal")
            .build()
            ).toString())
          .when().post("/zoe-ide-services/links")
          .then()
            .extract()
            .response()
            .body()
            .path("id");
  
    RestAssured.given()
    .body( 
        JsonObject.mapFrom(
            ImmutableCreateLocale.builder()
            .locale("en")
            .build()
            ).toString())
          .when().post("/zoe-ide-services/locales")
          .then().statusCode(200);
    
    RestAssured.given() 
    .body(
        JsonObject.mapFrom(
            ImmutableCreateWorkflow.builder()
            .locale("en")
            .content("cool name")
            .name("workflow name")
            .build()
            ).toString())
          .when().post("/zoe-ide-services/workflows")
          .then().statusCode(200);
    
    RestAssured.given()
    .body(
        JsonObject.mapFrom(
            ImmutableCreateRelease.builder()
            .name("v1.0")
            .note("init release")
            .build()
            ).toString())
          .when().post("/zoe-ide-services/releases")
          .then().statusCode(200);
    
    RestAssured.given()
    .body(
        JsonObject.mapFrom(
            ImmutableArticleMutator.builder()
            .articleId(articleId)
            .name("new name")
            .order(300)
            .build()
            ).toString())
          .when().put("/zoe-ide-services/articles/")
          .then().statusCode(200);
    
    RestAssured.given()
    .body(
         JsonObject.mapFrom(
            ImmutablePageMutator.builder()
            .pageId(pageId)
            .content("# new content")
            .locale("sv")
            .build()
            ).toString())
          .when().put("/zoe-ide-services/pages")
          .then().statusCode(200);
    
    
    RestAssured.given()
    .body(
         JsonObject.mapFrom(
            ImmutableLinkMutator.builder()
            .linkId(linkId)
            .content("# new content")
            .locale("sv")
            .description("stuff")
            .type("internal")
            .build()
            ).toString())
          .when().put("/zoe-ide-services/links")
          .then().statusCode(200);
    
   
  }
  
}
