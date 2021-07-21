package io.github.jocelynmutso.zoe.quarkus.ide;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import io.quarkus.deployment.configuration.ConfigurationError;
import io.quarkus.deployment.util.FileUtil;

public class IndexFactory {

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private String frontendPath;
    private String server;
    private String indexFileContent;

    public Builder frontend(String frontendPath) {
      this.frontendPath = frontendPath;
      return this;
    }
    public Builder server(String backendPath) {
      this.server = backendPath;
      return this;
    }
    public Builder index(Path path) {
      File file = path.toFile();
      try(InputStream stream = new FileInputStream(file)) {
        byte[] bytes = FileUtil.readFileContents(stream);
        this.indexFileContent = new String(bytes, StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new ConfigurationError(new StringBuilder("Failed to create frontend index.html, ")
            .append("msg = ").append(e.getMessage()).append(System.lineSeparator()).append(",")
            .append("path = ").append(path).append("!")
            .toString());
      }
      return this;
    }
    public Builder index(byte[] indexFileContent) {
      this.indexFileContent = new String(indexFileContent, StandardCharsets.UTF_8);
      return this;
    }
    public byte[] build() {
      PortalAssert.notEmpty(frontendPath, () -> "define frontendPath!");
      PortalAssert.notEmpty(server, () -> "define server!");
       
      String newPath = frontendPath.startsWith("/") ? frontendPath : "/" + frontendPath;
      newPath = newPath.endsWith("/") ? newPath : newPath + "/";
      StringBuilder newHref = new StringBuilder().append(newPath);
      StringBuilder newConfig = new StringBuilder()
          .append("const portalconfig={")
          .append("server: { url: '" + server + "' }, ")
          .append("}");  
      
      return (indexFileContent
          .replaceAll("/portal/", newPath)
          .replaceAll("https://portalconfig/", newHref.toString())
          .replaceFirst("const portalconfig=\\{\\}", newConfig.toString())
          + "<!-- NEW - PATH: " + newPath + "-->")
          .getBytes(StandardCharsets.UTF_8);
    }
    
  }
}
