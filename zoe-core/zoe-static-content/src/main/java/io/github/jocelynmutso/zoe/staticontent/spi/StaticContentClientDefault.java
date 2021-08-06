package io.github.jocelynmutso.zoe.staticontent.spi;

import java.io.IOException;

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

import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteState;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableImageData;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableLinkData;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableTopicData;
import io.github.jocelynmutso.zoe.staticontent.api.ImmutableTopicNameData;
import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent;
import io.github.jocelynmutso.zoe.staticontent.api.SiteContent;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient;
import io.github.jocelynmutso.zoe.staticontent.spi.beans.MutableStaticContent;
import io.github.jocelynmutso.zoe.staticontent.spi.support.ParserAssert;
import io.github.jocelynmutso.zoe.staticontent.spi.visitor.SiteStateVisitor;
import io.github.jocelynmutso.zoe.staticontent.spi.visitor.SiteVisitor;
import io.github.jocelynmutso.zoe.staticontent.spi.visitor.SiteVisitorDefault;

public class StaticContentClientDefault implements StaticContentClient {

  private static ObjectMapper objectMapper = new ObjectMapper();
  static {
    objectMapper.registerModule(new GuavaModule());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());
  }
  
  
  @Override
  public StaticContentBuilder create() {
    return new StaticContentBuilder() {
      private final SiteVisitor visitor = new SiteVisitorDefault((src) -> {
        try {
          return objectMapper.writeValueAsString(src);
        } catch(IOException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      });
      private String imageUrl;
      private Long created;
      @Override
      public StaticContentBuilder topic(
          Function<ImmutableTopicData.Builder, TopicData> newTopic) {
        visitor.visitTopicData(newTopic.apply(ImmutableTopicData.builder()));
        return this;
      }
      @Override
      public StaticContentBuilder link(
          Function<ImmutableLinkData.Builder, LinkData> newLink) {
        visitor.visitLinkData(newLink.apply(ImmutableLinkData.builder()));
        return this;
      }
      @Override
      public StaticContentBuilder image(
          Function<ImmutableImageData.Builder, ImageData> newImage) {
        visitor.visitImageData(newImage.apply(ImmutableImageData.builder()));
        return this;
      }
      @Override
      public StaticContentBuilder topicName(
          Function<ImmutableTopicNameData.Builder, TopicNameData> newTopicNames) {
        visitor.visitTopicNameData(newTopicNames.apply(ImmutableTopicNameData.builder()));
        return this;
      }
      @Override
      public StaticContentBuilder imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
      }
      @Override
      public StaticContentBuilder created(long created) {
        this.created = created;
        return this;
      }
      @Override
      public SiteContent build() {
        ParserAssert.notEmpty(imageUrl, () -> "imageUrl can't be empty!");
        ParserAssert.notNull(created, () -> "created can't be empty!");
        final var visited = visitor.visit(imageUrl);
        
        final var content = visited.getSites().stream().collect(
            Collectors.toMap(e -> e.getLocale(), e -> e)
        );
        return new MutableStaticContent(created, content);
      }
    };
  }

  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public StaticContentClientDefault build() {
      return new StaticContentClientDefault();
    }
  }

  @Override
  public StaticContentBuilder from(SiteState site) {
    final var content = new SiteStateVisitor().visit(site);
    return from(content);
  }
  
  @Override
  public StaticContentBuilder from(MarkdownContent content) {
    final var client = StaticContentClientDefault.builder().build().create();
    content.getValues()
    .forEach(value -> client.topic(builder -> builder
        .path(value.getPath())
        .locale(value.getLocale())
        .headings(value.getHeadings())
        .images(value.getImages())
        .value(value.getValue())
        .build()));
  
    content.getLinks()
    .forEach(link -> link.getLocale().forEach(locale -> client.link(builder -> builder
          .id(link.getId())
          .path(link.getPath())
          .locale(locale)
          .type(link.getType())
          .name(link.getDesc())
          .value(link.getValue())
          .workflow(link.getType().equals(SiteStateVisitor.LINK_TYPE_WORKFLOW))
          .build()
        )));
    return client;
  }


  @Override
  public SiteState parseSiteState(String json) {
    try {
      return objectMapper.readValue(json, SiteState.class);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  
  }
  @Override
  public MarkdownContent parseMd(SiteState site) {
    final var content = new SiteStateVisitor().visit(site);
    return content;
  }
}
