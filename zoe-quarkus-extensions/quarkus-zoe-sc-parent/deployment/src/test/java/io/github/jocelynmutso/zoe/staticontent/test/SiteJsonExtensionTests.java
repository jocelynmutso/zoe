package io.github.jocelynmutso.zoe.staticontent.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/*-
 * #%L
 * hdes-ui-quarkus-deployment
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;


//-Djava.util.logging.manager=org.jboss.logmanager.LogManager
public class SiteJsonExtensionTests {
  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addAsResource(new StringAsset(getSite()), "site.json")
      .addAsResource(new StringAsset(
          "quarkus.zoe-sc.site-json=site.json\r\n" +
          "quarkus.zoe-sc.service-path=portal/site\r\n" +
          ""), "application.properties")
    );

  @Test
  public void getUIOnRoot() {
    final var defaultLocale = RestAssured.when().get("/portal/site");
    defaultLocale.prettyPrint();
    defaultLocale.then().statusCode(200);
  }
  
  public static String getSite() {
    try {
      return IOUtils.toString(SiteJsonExtensionTests.class.getClassLoader().getResource("site.json"), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
