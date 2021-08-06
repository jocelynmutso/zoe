package io.github.jocelynmutso.zoe.staticontent.tests;

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.SiteState;
import io.github.jocelynmutso.zoe.staticontent.api.StaticContentClient;
import io.github.jocelynmutso.zoe.staticontent.spi.StaticContentClientDefault;
import io.github.jocelynmutso.zoe.staticontent.tests.config.TestUtils;

public class SiteTest {  
  final StaticContentClient client = StaticContentClientDefault.builder().build();
  
  @Test
  public void buildSite() {
    final SiteState site = TestUtils.getSite("site.json");

    final var content = StaticContentClientDefault.builder().build()
        .from(site).imageUrl("/images").created(1l)
        .build();
  
    
    String expected = TestUtils.toString("content.json");
    String actual = TestUtils.prettyPrint(content);
    Assertions.assertEquals(expected, actual);
    
  }
}
