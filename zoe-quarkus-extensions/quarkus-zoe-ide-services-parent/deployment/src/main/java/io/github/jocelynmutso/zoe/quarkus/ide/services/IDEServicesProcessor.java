package io.github.jocelynmutso.zoe.quarkus.ide.services;

/*-
 * #%L
 * quarkus-zoe-ide-services-deployment
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

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreatePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateRelease;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateWorkflow;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntity;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableEntityRepo;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLink;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLinkArticlePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLinkMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLocale;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableLocaleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePageMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableRelease;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflow;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflowArticlePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableWorkflowMutator;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.vertx.http.deployment.BodyHandlerBuildItem;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;



public class IDEServicesProcessor {
  IDEServicesConfig config;
  
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(IDEServicesRecorder.FEATURE_BUILD_ITEM);
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void buildtimeInit(
      IDEServicesBuildItem buildItem,
      IDEServicesRecorder recorder,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {
    
    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(IDEServicesProducer.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.configureBuildtimeConfig(
        buildItem.getServicePath(), 
        buildItem.getArticlesPath(), 
        buildItem.getPagesPath(), 
        buildItem.getWorkflowsPath(), 
        buildItem.getLinksPath(), 
        buildItem.getReleasesPath(), 
        buildItem.getLocalePath())));
  }

  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  void runtimeInit(
      RuntimeConfig config,
      IDEServicesBuildItem buildItem,
      IDEServicesRecorder recorder,
      
      BeanContainerBuildItem beanContainer, 
      ShutdownContextBuildItem shutdown) {
    
    recorder.configureRuntimeConfig(config);
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  public void staticContentHandler(
    IDEServicesBuildItem buildItem,
    IDEServicesRecorder recorder,
    HttpRootPathBuildItem httpRoot,
    BuildProducer<RouteBuildItem> routes,
    BodyHandlerBuildItem body,
    IDEServicesConfig config) throws Exception {
    
    final var bodyHandler = body.getHandler();
    final Handler<RoutingContext> handler = recorder.ideServicesHandler();
    
    routes.produce(httpRoot.routeBuilder()
        .routeFunction(buildItem.getServicePath(), recorder.routeFunction(bodyHandler))
        .handler(handler)
        .build());
    routes.produce(httpRoot.routeBuilder()
        .routeFunction(buildItem.getServicePath() + "/*", recorder.routeFunction(bodyHandler))
        .handler(handler)
        .build());
  }
  
  @BuildStep
  public ReflectiveClassBuildItem reflection() {
    return new ReflectiveClassBuildItem(true, true,
        ImmutableEntityRepo.class,
        ImmutableArticle.class,
        ImmutableArticleMutator.class,
        ImmutableCreateArticle.class,
        ImmutableCreateLink.class,
        ImmutableCreateLocale.class,
        ImmutableCreatePage.class,
        ImmutableCreateRelease.class,
        ImmutableCreateWorkflow.class,
        ImmutableEntity.class,
        ImmutableLink.class,
        ImmutableLinkArticlePage.class,
        ImmutableLinkMutator.class,
        ImmutableLocale.class,
        ImmutableLocaleMutator.class,
        ImmutablePage.class,
        ImmutablePageMutator.class,
        ImmutableRelease.class,
        ImmutableWorkflow.class,
        ImmutableWorkflowArticlePage.class,
        ImmutableWorkflowMutator.class);
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void frontendBeans(
      IDEServicesRecorder recorder,
      BuildProducer<IDEServicesBuildItem> buildProducer,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    final var servicePath = cleanPath(config.servicePath);
    final var buildItem = IDEServicesBuildItem.builder(servicePath)
        .articlesPath("articles")
        .pagesPath("pages")
        .localePath("locales")
        .workflowsPath("workflows")
        .linksPath("links")
        .releasesPath("releases")
        .build();
    
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(servicePath), "ZOE Actions"));
    buildProducer.produce(buildItem);
  }
  
  private static String cleanPath(String value) {
    return IDEServicesProducer.cleanPath(value);
  }
}
