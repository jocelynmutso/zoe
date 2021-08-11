package io.github.jocelynmutso.zoe.staticontent.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

//-Djava.util.logging.manager=org.jboss.logmanager.LogManager
public class WebJarExtensionTests {

  @RegisterExtension
  final static QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
      .addAsResource(new StringAsset(
          "quarkus.zoe-sc.webjar=io.github.jocelynmutso:quarkus-zoe-sc-testcontent\r\n" +
          "quarkus.zoe-sc.service-path=portal/site\r\n" +
          ""), "application.properties")
    );

  @Test
  public void getUIOnRoot() {
    final var defaultLocale = RestAssured.when().get("/portal/site");
    defaultLocale.prettyPrint();
    defaultLocale.then().statusCode(200);
  }
}
