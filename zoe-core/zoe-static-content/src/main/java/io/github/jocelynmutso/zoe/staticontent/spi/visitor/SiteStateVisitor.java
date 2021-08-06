package io.github.jocelynmutso.zoe.staticontent.spi.visitor;

/*-
 * #%L
 * zoe-static-content
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteState;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableLinkResource;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableMarkdown;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableMarkdownContent;
import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent;
import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent.LinkResource;
import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent.Markdown;

public class SiteStateVisitor {
  
  public static String LINK_TYPE_WORKFLOW = "workflow";
  private final List<Entity<Locale>> locales = new ArrayList<>();
  private SiteState entity;
  
  public MarkdownContent visit(SiteState entity) {
    this.entity = entity;
    final var result = ImmutableMarkdownContent.builder()
        .addAllLocales(visitLocales(entity).stream().map(e -> e.getBody().getValue()).collect(Collectors.toList()));
    
    for(final var article : entity.getArticles().values()) {
      result.addAllValues(visitArticle(article));
    }
    
    for(final var link : entity.getLinks().values()) {
      result.addAllLinks(visitLinks(link));
    }
    for(final var link : entity.getWorkflows().values()) {
      result.addAllLinks(visitWorkflows(link));
    }
    
    return result.build();
  }

  private List<LinkResource> visitWorkflows(Entity<Workflow> link) {
    final List<LinkResource> result = new ArrayList<>();
    
    for(final var articleId : link.getBody().getArticles()) {
      final var locale = locales.stream().filter(l -> link.getBody().getLocale().equals(l.getId())).findFirst();
      if(locale.isEmpty()) {
        continue;
      }
      
      final var article = entity.getArticles().get(articleId);
      final var resource = ImmutableLinkResource.builder()
          .id(link.getId() + "-" + locale.get().getBody().getValue())
          .addLocale(locale.get().getBody().getValue())
          .desc(link.getBody().getName())
          .path(visitArticlePath(article))
          .value(link.getBody().getContent())
          .workflow(true)
          .type(LINK_TYPE_WORKFLOW)
          .build();
      result.add(resource);
    }
    return result;
  }
  
  private List<LinkResource> visitLinks(Entity<Link> link) {
    final List<LinkResource> result = new ArrayList<>();
    
    for(final var articleId : link.getBody().getArticles()) {
      final var locale = locales.stream().filter(l -> link.getBody().getLocale().equals(l.getId())).findFirst();
      if(locale.isEmpty()) {
        continue;
      }
      
      final var article = entity.getArticles().get(articleId);
      final var resource = ImmutableLinkResource.builder()
          .id(link.getId() + "-" + locale.get().getBody().getValue())
          .addLocale(locale.get().getBody().getValue())
          .desc(link.getBody().getDescription())
          .path(visitArticlePath(article))
          .value(link.getBody().getContent())
          .workflow(false)
          .type(link.getBody().getContentType())
          .build();
      result.add(resource);
    }
    return result;
  }
  
  private List<Markdown> visitArticle(Entity<Article> article) {
    final String path = visitArticlePath(article);
    final List<Markdown> result = new ArrayList<>();
    for(final var page : entity.getPages().values()) {
      if(!page.getBody().getArticle().equals(article.getId())) {
        continue;
      }
      final var locale = locales.stream().filter(l -> page.getBody().getLocale().equals(l.getId())).findFirst();
      if(locale.isEmpty()) {
        continue;
      }
      
      final var content = page.getBody().getContent();
      final var ast = new MarkdownVisitor().visit(content);
      if(ast.getHeadings().stream().filter(entity -> entity.getLevel() == 1).findFirst().isEmpty()) {
        throw new MarkdownException("markdown must have atleast one h1(line starting with one # my super menu)");
      }
      
      result.add(ImmutableMarkdown.builder()
          .path(path)
          .locale(locale.get().getBody().getValue())
          .value(content)
          .addAllHeadings(ast.getHeadings())
          .build());
    }
    
    return result;
  }
  
  private String visitArticlePath(Entity<Article> src) {

    final StringBuilder path = new StringBuilder();
    Entity<Article> article = src;
    do {
      if(path.length() > 0) {
        path.append("/");
      }
      path.append(article.getBody().getName());
      final var parentId = article.getBody().getParentId();
      article = parentId == null ? null : entity.getArticles().get(parentId);
    } while(article != null);

    return path.toString();
  }
  
  
  private List<Entity<Locale>> visitLocales(SiteState site) {
    this.locales.addAll(site.getLocales().values().stream()
        .filter(l -> l.getBody().getEnabled())
        .collect(Collectors.toList()));
    return locales;
  }
}
