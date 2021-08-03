package io.github.jocelynmutso.zoe.quarkus.ide.services;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class DBConfig {

  /**
   * Database name
   */
  @ConfigItem
  String dbName;
  
  /**
   * Database connection URL
   */
  @ConfigItem
  String connectionUrl;
}
