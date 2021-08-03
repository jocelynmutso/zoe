package io.github.jocelynmutso.zoe.quarkus.ide.services;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.inject.spi.CDI;

import io.github.jocelynmutso.zoe.quarkus.ide.services.handlers.IDEServicesResourceHandler;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class IDEServicesRecorder {
  public static final String FEATURE_BUILD_ITEM = "zoe-ide-services";
  
  public BeanContainerListener configureBuildtimeConfig(
      String servicePath,
      String articlesPath,
      String pagesPath,
      String workflowsPath,
      String linksPath,
      String releasesPath,
      String localePath) {
    
    return beanContainer -> {
      IDEServicesProducer producer = beanContainer.instance(IDEServicesProducer.class);
      producer
        .setArticlesPath(articlesPath)
        .setLinksPath(linksPath)
        .setLocalePath(localePath)
        .setPagesPath(pagesPath)
        .setReleasesPath(releasesPath)
        .setServicePath(servicePath)
        .setWorkflowsPath(workflowsPath);
    };
  }
  
  public void configureRuntimeConfig(RuntimeConfig runtimeConfig) {
    CDI.current().select(IDEServicesProducer.class).get().setRuntimeConfig(runtimeConfig);
  }

  public Handler<RoutingContext> ideServicesHandler() {
    final var identityAssociations = CDI.current().select(CurrentIdentityAssociation.class);
    CurrentIdentityAssociation association;
    if (identityAssociations.isResolvable()) {
      association = identityAssociations.get();
    } else {
      association = null;
    }
    CurrentVertxRequest currentVertxRequest = CDI.current().select(CurrentVertxRequest.class).get();
    return new IDEServicesResourceHandler(association, currentVertxRequest);
  }

  public Consumer<Route> routeFunction(Handler<RoutingContext> bodyHandler) {
    return (route) -> route.handler(bodyHandler);
  }
  
  public Function<Router, Route> routeFunction(String rootPath, Handler<RoutingContext> bodyHandler) {
    return new Function<Router, Route>() {
      @Override
      public Route apply(Router router) {
        return router.route(rootPath).handler(bodyHandler);
      }
    };
  }
}
