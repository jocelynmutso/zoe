package io.github.jocelynmutso.zoe.quarkus.ide.services;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = IDEServicesRecorder.FEATURE_BUILD_ITEM)
public class IDEServicesConfig {
  
  /**
   * Static content routing path
   */
  @ConfigItem(defaultValue = "zoe-ide-services")
  String servicePath;
}
