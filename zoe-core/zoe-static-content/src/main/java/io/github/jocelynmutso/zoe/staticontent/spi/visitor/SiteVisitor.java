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

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.github.jocelynmutso.zoe.staticontent.api.SiteContent.Site;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient.ImageData;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient.LinkData;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient.TopicData;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient.TopicNameData;

public interface SiteVisitor {
  void visitTopicData(TopicData topic);
  void visitLinkData(LinkData link);
  void visitImageData(ImageData image);
  void visitTopicNameData(TopicNameData names);
  Sites visit(String imageUrl);
  
  @Value.Immutable
  interface Sites {
    List<Message> getMessage();
    List<Site> getSites();
  }
  
  @Value.Immutable
  interface Message {
    String getText();
    @Nullable
    Object getObject();
  }
}
