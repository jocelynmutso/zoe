package io.github.jocelynmutso.zoe.staticontent.api;

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
import java.util.Map;

import javax.annotation.Nullable;


public interface SiteContent {
  Long getCreated();
  Map<String, Site> getSites();

  interface Site {
    String getId();
    String getImages();
    String getLocale();
    
    Map<String, Topic> getTopics();
    Map<String, Blob> getBlobs();
    Map<String, TopicLink> getLinks();
  }

  interface Blob {
    String getId();
    String getValue();
  }

  interface Topic {
    String getId();
    String getName();
    List<String> getLinks();
    List<TopicHeading> getHeadings();
    @Nullable
    String getParent();
    @Nullable
    String getBlob();
  }

  interface TopicHeading {
    String getId();
    String getName();
    Integer getOrder();
    Integer getLevel();
  }
  
  interface TopicLink {
    String getId();
    String getType();
    String getName();
    String getValue();
    Boolean getSecured();
  }
}
