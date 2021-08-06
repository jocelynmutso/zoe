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

import io.github.jocelynmutso.zoe.staticontent.api.MarkdownContent;
import io.quarkus.builder.item.SimpleBuildItem;

public final class StaticContentBuildItem extends SimpleBuildItem {
  private final String projectsUiFinalDestination;
  private final String projectsUiPath;
  private final MarkdownContent content;

  public StaticContentBuildItem(String projectsUiFinalDestination, String projectsUiPath, MarkdownContent content) {
    super();
    this.projectsUiFinalDestination = projectsUiFinalDestination;
    this.projectsUiPath = projectsUiPath;
    this.content = content;
  }

  public MarkdownContent getContent() {
    return content;
  }
  public String getUiFinalDestination() {
    return projectsUiFinalDestination;
  }
  public String getUiPath() {
    return projectsUiPath;
  }
}
