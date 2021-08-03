package io.github.jocelynmutso.zoe.quarkus.ide.services;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class RepoConfig {
  
  /**
   * Repository name
   */
  @ConfigItem
  String repoName;
  
  /**
   * working branch/head name
   */
  @ConfigItem(defaultValue = "main")
  String headName;
}
