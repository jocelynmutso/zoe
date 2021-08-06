package io.github.jocelynmutso.zoe.staticontent;

/*-
 * #%L
 * quarkus-zoe-sc-deployment
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteState;
import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent;
import io.github.jocelynmutso.zoe.staticontent.spi.StaticContentClientDefault;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.configuration.ConfigurationError;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class StaticContentProcessor {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(StaticContentProcessor.class);
  
  private static final String FINAL_DESTINATION = "META-INF/zoe-sc-files";
  public static final String FEATURE_BUILD_ITEM = "zoe-sc";
  
  @Inject
  private LaunchModeBuildItem launch;
  
  StaticContentConfig config;
  
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE_BUILD_ITEM);
  }

  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  void backendBeans(
      StaticContentBuildItem buildItem,
      StaticContentRecorder recorder,
      BuildProducer<AdditionalBeanBuildItem> buildItems,
      BuildProducer<BeanContainerListenerBuildItem> beans) {

    final var client = StaticContentClientDefault.builder().build()
        .from(buildItem.getContent())
        .imageUrl(buildItem.getUiPath())
        .created(System.currentTimeMillis());
    final var content = client.build();
    final var contentValues = content.getSites().entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> Json.encode(e.getValue())));
    
    if(!contentValues.containsKey(config.defaultLocale)) {
      throw new ConfigurationError("Markdowns must have localization for default-locale: '" + config.defaultLocale + "'!");
    }
    
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Supported locales: '" + String.join(", ", contentValues.keySet()) + "'");
    }
    buildItems.produce(AdditionalBeanBuildItem.builder().setUnremovable().addBeanClass(StaticContentBeanFactory.class).build());
    beans.produce(new BeanContainerListenerBuildItem(recorder.listener(content, contentValues, config.defaultLocale)));
  }
  
  @BuildStep
  @Record(ExecutionTime.RUNTIME_INIT)
  public void staticContentHandler(
    StaticContentRecorder recorder,
    HttpRootPathBuildItem httpRoot,
    BuildProducer<RouteBuildItem> routes,
    StaticContentConfig config) throws Exception {
    
    Handler<RoutingContext> handler = recorder.staticContentHandler();

    routes.produce(httpRoot.routeBuilder()
        .route(config.servicePath)
        .handler(handler)
        .build());
    routes.produce(httpRoot.routeBuilder()
        .route(config.servicePath + "/*")
        .handler(handler)
        .build());
  }
  
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  public void frontendBeans(
      StaticContentRecorder recorder,
      BuildProducer<StaticContentBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {

    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(config.servicePath), "Zoe Static Content"));
    
    
    // dev envir    
    if (launch.getLaunchMode().isDevOrTest()) {
      
      Path tempPath = this.config.siteJson;
      SiteState site;
      try {
        
//          byte[] bytes = Files.readAllBytes(tempPath);
//          site = StaticContentClientDefault.builder().build().parseSiteState(new String(bytes, StandardCharsets.UTF_8));
          final var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(tempPath.toString());
          String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
          site = StaticContentClientDefault.builder().build().parseSiteState(content);
        
      } catch(IOException e) {
        throw new ConfigurationError("Failed to read file: '" + tempPath + "'!");
      }
      
      final MarkdownContent md = StaticContentClientDefault.builder().build().parseMd(site);
      final String frontendPath = httpRootPathBuildItem.resolvePath(config.imagePath);
      buildProducer.produce(new StaticContentBuildItem(tempPath.toAbsolutePath().toString(), frontendPath, md));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(frontendPath + "/"), "Zoe Static Content"));
      return;
    } 
    
    
    
    // native image
    final String frontendPath = httpRootPathBuildItem.resolvePath(config.imagePath);
    Path tempPath = this.config.siteJson;
    final SiteState site;
    
    try {
      final var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(tempPath.toString());
      String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
      site = StaticContentClientDefault.builder().build().parseSiteState(content);
    } catch(IOException e) {
      throw new ConfigurationError("Failed to read file: '" + tempPath + "'!");
    }

    String fileName = tempPath.toFile().getName().toString();
    fileName = FINAL_DESTINATION + "/" + fileName;
    
    /* copy images
    if(cleanFileName.startsWith("images/")) {
      generatedResources.produce(new GeneratedResourceBuildItem(fileName, bytes));
      nativeImage.produce(new NativeImageResourceBuildItem(fileName));
    }
    */
    
    final MarkdownContent md = StaticContentClientDefault.builder().build().parseMd(site);
    buildProducer.produce(new StaticContentBuildItem(FINAL_DESTINATION, frontendPath, md));
  }
}
