package io.github.jocelynmutso.zoe.quarkus.ide.services.handlers;

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
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.EntityBody;
import io.github.jocelynmutso.zoe.quarkus.ide.services.IDEServicesContext;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.smallrye.mutiny.Uni;
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
    final var client = ctx.getClient();
    
    if(path.endsWith(ctx.getPaths().getServicePath())) {
      if (event.request().method() == HttpMethod.POST) {
        client.create().repo()
        .onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
        .onFailure().invoke(e -> catch422(e, ctx, response))
        .subscribe().with(data -> response.end(data)); 
      } else {
        catch404("unsupported repository action", ctx, response);
      }
      
    } else if (path.startsWith(ctx.getPaths().getArticlesPath())) {
      
      // ARTICLES
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().article(read(event, ImmutableCreateArticle.class)), 
            response, ctx);
        
      } else if(event.request().method() == HttpMethod.PUT) {
        subscribe(
            client.update().article(read(event, ImmutableArticleMutator.class)),
            response, ctx);
      } else if(event.request().method() == HttpMethod.DELETE) {
        subscribe(
            client.delete().article(getId(path)),
            response, ctx);
      } else {
        catch404("unsupported article action", ctx, response);
      }
      
      
    } else if(path.startsWith(ctx.getPaths().getLinksPath())) {
      
      // LINKS
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().link(read(event, ImmutableCreateLink.class)), 
            response, ctx);
        
      } else if(event.request().method() == HttpMethod.PUT) {
        subscribe(
            client.update().link(read(event, ImmutableLinkMutator.class)),
            response, ctx);
      } else if(event.request().method() == HttpMethod.DELETE) {
        subscribe(
            client.delete().link(getId(path)),
            response, ctx);
      } else {
        catch404("unsupported links action", ctx, response);
      }
    
      
    } else if(path.startsWith(ctx.getPaths().getLocalePath())) {
      
      // LOCALES
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().locale(read(event, ImmutableCreateLocale.class)), 
            response, ctx);
        
      } else if(event.request().method() == HttpMethod.PUT) {
        subscribe(
            client.update().locale(read(event, ImmutableLocaleMutator.class)),
            response, ctx);
      } else if(event.request().method() == HttpMethod.DELETE) {
        subscribe(
            client.delete().locale(getId(path)),
            response, ctx);
      } else {
        catch404("unsupported locale action", ctx, response);
      }
      
      
    } else if(path.startsWith(ctx.getPaths().getReleasesPath())) {

      // RELEASES
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().release(read(event, ImmutableCreateRelease.class)), 
            response, ctx);
      } else {
        catch404("unsupported release action", ctx, response);
      }
      
    } else if(path.startsWith(ctx.getPaths().getWorkflowsPath())) {
      
      // WORKFLOWS
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().workflow(read(event, ImmutableCreateWorkflow.class)), 
            response, ctx);
        
      } else if(event.request().method() == HttpMethod.PUT) {
        subscribe(
            client.update().workflow(read(event, ImmutableWorkflowMutator.class)),
            response, ctx);
      } else if(event.request().method() == HttpMethod.DELETE) {
        subscribe(
            client.delete().workflow(getId(path)),
            response, ctx);
      } else {
        catch404("unsupported workflow action", ctx, response);
      }
      
      
    } else if(path.startsWith(ctx.getPaths().getPagesPath())) {
      
      // PAGES
      
      if (event.request().method() == HttpMethod.POST) {
        subscribe(
            client.create().page(read(event, ImmutableCreatePage.class)),
            response, ctx);
      } else if(event.request().method() == HttpMethod.PUT) {
        subscribe(
            client.update().page(read(event, ImmutablePageMutator.class)),
            response, ctx);
        
      } else if(event.request().method() == HttpMethod.DELETE) {
        subscribe(
            client.delete().page(getId(path)),
            response, ctx);
 
      } else {
        catch404("unsupported page action", ctx, response);
      }
    } else {
      catch404("unsupported action", ctx, response);
    }
  }
  
  public <T> T read(RoutingContext event, Class<T> type) {
    return new JsonObject(event.getBody()).mapTo(type);
  }

  public <T extends EntityBody> void subscribe(Uni<Entity<T>> uni, HttpServerResponse response, IDEServicesContext ctx) {
    uni.onItem().transform(data -> JsonObject.mapFrom(data).toBuffer())
    .onFailure().invoke(e -> catch422(e, ctx, response))
    .subscribe().with(data -> response.end(data)); 
  }
  
  private String getId(String path) {
    final var index = path.lastIndexOf("/");
    if(index < -1 || path.length() > index + 1) {
      return "";
    }
    return path.substring(index+1);
  }
}
