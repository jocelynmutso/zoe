package io.github.jocelynmutso.zoe.quarkus.ide.services;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = IDEServicesRecorder.FEATURE_BUILD_ITEM)
public class RuntimeConfig {

  /**
   * Configuration for database
   */
  @ConfigItem
  DBConfig db;

  /**
   * Configuration for working repository
   */
  @ConfigItem
  RepoConfig repo;
}
