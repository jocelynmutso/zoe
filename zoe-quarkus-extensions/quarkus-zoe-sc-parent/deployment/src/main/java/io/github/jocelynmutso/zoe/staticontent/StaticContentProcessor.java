package io.github.jocelynmutso.zoe.staticontent;

import java.io.FileInputStream;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
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
import io.quarkus.bootstrap.model.AppArtifact;
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
import io.quarkus.deployment.util.FileUtil;
import io.quarkus.deployment.util.WebJarUtil;
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

    
    if(config.siteJson.isPresent() && config.webjar.isPresent()) {
      throw new ConfigurationError("siteJson and webjar both can't be defined, define only one of them!"); 
    }

    if(config.siteJson.isEmpty() && config.webjar.isEmpty()) {
      throw new ConfigurationError("siteJson and webjar both are empty, define one of them!"); 
    }
    
    
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
  public void staticContent(
      StaticContentRecorder recorder,
      BuildProducer<StaticContentBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {
    
    

    if(this.config.siteJson.isPresent()) {
      staticJSONContent(recorder, buildProducer, generatedResources, nativeImage, nonApplicationRootPathBuildItem, curateOutcomeBuildItem, liveReloadBuildItem, httpRootPathBuildItem, displayableEndpoints);
      return;
    }
   
    if(this.config.webjar.isPresent()) {
      staticWebjarContent(recorder, buildProducer, generatedResources, nativeImage, nonApplicationRootPathBuildItem, curateOutcomeBuildItem, liveReloadBuildItem, httpRootPathBuildItem, displayableEndpoints);
      return;
    }
  }
  
  public void staticWebjarContent(
      StaticContentRecorder recorder,
      BuildProducer<StaticContentBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {

    
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(config.servicePath), "Zoe Static Content From Webjar"));
    
    final String[] fragments = config.webjar.get().split(":");
    final String webjarGroupId = fragments[0];
    final String webjarArtifactId = fragments[1];
    final String webjarPrefix = "META-INF/resources/webjars/" + webjarArtifactId + "/";
    
    // dev envir
    final AppArtifact artifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem, webjarGroupId, webjarArtifactId);    
    if (launch.getLaunchMode().isDevOrTest()) {
      
      Path tempPath = WebJarUtil
          .copyResourcesForDevOrTest(liveReloadBuildItem, curateOutcomeBuildItem, launch, artifact, webjarPrefix + artifact.getVersion(), false);
      String tempAbsolutePath = tempPath.toAbsolutePath().toString();
      
      final var builder = StaticContentClientDefault.builder().build().parseFiles();
      Files.walk(tempPath).filter(Files::isRegularFile).forEach(file -> {
        try {
          String absolutePath = file.toAbsolutePath().toString();
          String path = absolutePath.substring(tempAbsolutePath.length() + 1);
          byte[] bytes = FileUtil.readFileContents(new FileInputStream(file.toFile()));
          builder.add(path, bytes);
        } catch(IOException e) {
          throw new ConfigurationError("Failed to read file: '" + file + "'!");
        }
      });

      final String frontendPath = httpRootPathBuildItem.resolvePath(config.imagePath);
      buildProducer.produce(new StaticContentBuildItem(tempPath.toAbsolutePath().toString(), frontendPath, builder.build()));
      displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(frontendPath + "/"), "Zoe Static Content From Webjar"));

      // Handle live reload of branding files
      if (liveReloadBuildItem.isLiveReload() && !liveReloadBuildItem.getChangedResources().isEmpty()) {
          WebJarUtil.hotReloadBrandingChanges(
              curateOutcomeBuildItem, launch, artifact,
                  liveReloadBuildItem.getChangedResources());
      }
      return;
    } 
      
    
    // native image
    final String frontendPath = httpRootPathBuildItem.resolvePath(config.imagePath);
    final Map<String, byte[]> files = WebJarUtil.copyResourcesForProduction(curateOutcomeBuildItem, artifact, webjarPrefix + artifact.getVersion());
    final var builder = StaticContentClientDefault.builder().build().parseFiles();
    
    for (Map.Entry<String, byte[]> file : files.entrySet()) {
      String fileName = file.getKey();
      byte[] content = file.getValue();
      fileName = FINAL_DESTINATION + "/" + fileName;
      final String cleanFileName = fileName.toLowerCase();
      
      // copy images
      if(cleanFileName.startsWith("images/")) {
        generatedResources.produce(new GeneratedResourceBuildItem(fileName, content));
        nativeImage.produce(new NativeImageResourceBuildItem(fileName));
      }
      builder.add(file.getKey(), file.getValue());
    }      
    buildProducer.produce(new StaticContentBuildItem(FINAL_DESTINATION, frontendPath, builder.build()));
    
  }
  
  public void staticJSONContent(
      StaticContentRecorder recorder,
      BuildProducer<StaticContentBuildItem> buildProducer,
      
      BuildProducer<GeneratedResourceBuildItem> generatedResources,
      BuildProducer<NativeImageResourceBuildItem> nativeImage,
      
      NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
      CurateOutcomeBuildItem curateOutcomeBuildItem,
      
      LiveReloadBuildItem liveReloadBuildItem,
      HttpRootPathBuildItem httpRootPathBuildItem,
      BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints) throws Exception {

    
    displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(httpRootPathBuildItem.resolvePath(config.servicePath), "Zoe Static Content From JSON"));
    Path tempPath = this.config.siteJson.get();
    
    // dev envir    
    if (launch.getLaunchMode().isDevOrTest()) {
      SiteState site;
      try {
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
    final MarkdownContent md = StaticContentClientDefault.builder().build().parseMd(site);
    buildProducer.produce(new StaticContentBuildItem(FINAL_DESTINATION, frontendPath, md));
  }
}
