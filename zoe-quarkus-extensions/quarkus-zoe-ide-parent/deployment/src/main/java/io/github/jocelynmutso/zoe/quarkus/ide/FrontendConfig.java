package io.github.jocelynmutso.zoe.quarkus.ide;

import io.quarkus.runtime.annotations.ConfigDocSection;

/*-
 * #%L
 * hdes-projects-quarkus-deployment
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
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

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = FrontendProcessor.FEATURE_BUILD_ITEM)
public class FrontendConfig {
  
  /**
   * IDE routing path
   */
  @ConfigItem(defaultValue = "portal-app")
  String servicePath;
  
  /**
   * IDE backend server path
   */
  @ConfigItem
  @ConfigDocSection
  String serverPath;
}
