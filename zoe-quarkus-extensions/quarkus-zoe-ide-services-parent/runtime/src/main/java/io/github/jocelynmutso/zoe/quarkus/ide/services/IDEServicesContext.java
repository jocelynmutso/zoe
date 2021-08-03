package io.github.jocelynmutso.zoe.quarkus.ide.services;

import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence;

public class IDEServicesContext {
  private final ZoePersistence client;
  private final ServicesPathConfig paths;
  
  public IDEServicesContext(ZoePersistence client, ServicesPathConfig paths) {
    super();
    this.client = client;
    this.paths = paths;
  }

  public ZoePersistence getClient() {
    return client;
  }

  public ServicesPathConfig getPaths() {
    return paths;
  }
}
