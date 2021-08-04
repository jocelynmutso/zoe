package io.github.jocelynmutso.zoe.quarkus.ide.services;

/*-
 * #%L
 * quarkus-zoe-ide-services
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

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.internal.ProvidersCodecRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClients;

import io.github.jocelynmutso.zoe.persistence.spi.ZoePersistenceImpl;
import io.github.jocelynmutso.zoe.persistence.spi.serializers.ZoeDeserializer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.quarkus.mongodb.impl.ReactiveMongoClientImpl;
import io.resys.thena.docdb.spi.DocDBCodecProvider;
import io.resys.thena.docdb.spi.DocDBFactory;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class IDEServicesProducer {

  private RuntimeConfig runtimeConfig;
  private String servicePath;
  private String articlesPath;
  private String pagesPath;
  private String workflowsPath;
  private String linksPath;
  private String releasesPath;
  private String localePath;
  
  public IDEServicesProducer setRuntimeConfig(RuntimeConfig runtimeConfig) {
    this.runtimeConfig = runtimeConfig;
    return this;
  }

  @Singleton
  public static class RegisterGuava implements ObjectMapperCustomizer {
    @Override
    public void customize(ObjectMapper objectMapper) {
      objectMapper.registerModule(new GuavaModule());
    }
  }

  @Produces
  @ApplicationScoped
  public RegisterGuava registerGuava() {
    return new RegisterGuava();
  }
  
  @Produces
  @ApplicationScoped
  public IDEServicesContext zoeIdeServicesContext(Vertx vertx, ObjectMapper objectMapper) {
    
    final var paths = ServicesPathConfig.builder()
      .articlesPath(articlesPath)
      .servicePath(servicePath)
      .pagesPath(pagesPath)
      .workflowsPath(workflowsPath)
      .linksPath(linksPath)
      .localePath(localePath)
      .releasesPath(releasesPath)
      .build();
    
    final var mongo = new ReactiveMongoClientImpl(MongoClients.create(
        MongoClientSettings.builder()
        .codecRegistry(new ProvidersCodecRegistry(Arrays.asList(new DocDBCodecProvider(),
            new DocumentCodecProvider(), new Jsr310CodecProvider(), new ValueCodecProvider())))
        .applyConnectionString(new ConnectionString(runtimeConfig.db.connectionUrl))
        .build()));
    
    final var docDb = DocDBFactory.create().db(runtimeConfig.db.dbName).client(mongo).build();
    final var deserializer = new ZoeDeserializer(objectMapper);
    final var client = ZoePersistenceImpl.builder()
        .config((builder) -> builder
            .client(docDb)
            .repoName(runtimeConfig.repo.repoName)
            .headName(runtimeConfig.repo.headName)
            .deserializer(deserializer)
            .serializer((entity) -> {
              try {
                return objectMapper.writeValueAsString(entity);
              } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
              }
            })
            .gidProvider(type -> UUID.randomUUID().toString())
            .authorProvider(() -> "no-author"))
        .build();
    
    
    // create repo if not present
    return new IDEServicesContext(client, paths);
  }
  
  public static String cleanPath(String value) {
    final var start = value.startsWith("/") ? value.substring(1) : value;
    return start.endsWith("/") ? value.substring(0, start.length() -2) : start;
  }

  public IDEServicesProducer setServicePath(String servicePath) {
    this.servicePath = servicePath;
    return this;
  }

  public IDEServicesProducer setArticlesPath(String articlesPath) {
    this.articlesPath = articlesPath;
    return this;
  }

  public IDEServicesProducer setPagesPath(String pagesPath) {
    this.pagesPath = pagesPath;
    return this;
  }

  public IDEServicesProducer setWorkflowsPath(String workflowsPath) {
    this.workflowsPath = workflowsPath;
    return this;
  }

  public IDEServicesProducer setLinksPath(String linksPath) {
    this.linksPath = linksPath;
    return this;
  }

  public IDEServicesProducer setReleasesPath(String releasesPath) {
    this.releasesPath = releasesPath;
    return this;
  }

  public IDEServicesProducer setLocalePath(String localePath) {
    this.localePath = localePath;
    return this;
  }
}
