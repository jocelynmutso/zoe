package io.github.jocelynmutso.zoe.quarkus.ide.services.handlers;

import io.github.jocelynmutso.zoe.persistence.api.ImmutableArticleMutator;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreateArticle;
import io.github.jocelynmutso.zoe.persistence.api.ImmutableCreatePage;
import io.github.jocelynmutso.zoe.persistence.api.ImmutablePageMutator;
import io.github.jocelynmutso.zoe.quarkus.ide.services.IDEServicesContext;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class IDEServicesResourceHandler extends HdesResourceHandler {

  public IDEServicesResourceHandler(CurrentIdentityAssociation currentIdentityAssociation,
      CurrentVertxRequest currentVertxRequest) {
    super(currentIdentityAssociation, currentVertxRequest);
  }

  @Override
  protected void handleResource(RoutingContext event, HttpServerResponse response, IDEServicesContext ctx) {
    response.headers().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
    final var path = event.normalisedPath();

    if(path.endsWith(ctx.getPaths().getServicePath())) {
      if (event.request().method() == HttpMethod.POST) {
        ctx.getClient()
        .create().repo()
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data)); 
      }
      
    } else if (path.startsWith(ctx.getPaths().getArticlesPath())) {
      
      // articles
      
      if (event.request().method() == HttpMethod.POST) {
        ctx.getClient()
        .create().article(new JsonObject(event.getBody()).mapTo(ImmutableCreateArticle.class))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data));
      } else if(event.request().method() == HttpMethod.PUT) {
        ctx.getClient()
        .update().article(new JsonObject(event.getBody()).mapTo(ImmutableArticleMutator.class))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data));
      } else if(event.request().method() == HttpMethod.DELETE) {
        ctx.getClient()
        .delete().article(getId(path))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data)); 
      }
    

    } else if(path.startsWith(ctx.getPaths().getPagesPath())) {
      
      // pages
      
      if (event.request().method() == HttpMethod.POST) {
        ctx.getClient()
        .create().page(new JsonObject(event.getBody()).mapTo(ImmutableCreatePage.class))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data));
      } else if(event.request().method() == HttpMethod.PUT) {
        ctx.getClient()
        .update().page(new JsonObject(event.getBody()).mapTo(ImmutablePageMutator.class))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data));
      } else if(event.request().method() == HttpMethod.DELETE) {
        ctx.getClient()
        .delete().page(getId(path))
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data)); 
      } 
    }
  }

  
  private String getId(String path) {
    final var index = path.lastIndexOf("/");
    if(index < -1 || path.length() > index + 1) {
      return "";
    }
    return path.substring(index+1);
  }
}
