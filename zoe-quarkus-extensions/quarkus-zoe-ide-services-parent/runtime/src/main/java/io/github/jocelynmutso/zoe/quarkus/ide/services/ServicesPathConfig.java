package io.github.jocelynmutso.zoe.quarkus.ide.services;

public class ServicesPathConfig {
  
  private final String servicePath;
  private final String articlesPath;
  private final String pagesPath;
  private final String workflowsPath;
  private final String linksPath;
  private final String releasesPath;
  private final String localePath;
  
  public ServicesPathConfig(
      String servicePath, 
      String articlesPath, 
      String pagesPath, 
      String workflowsPath,
      String linksPath, 
      String releasesPath,
      String localePath) {
    super();
    this.servicePath = servicePath;
    this.articlesPath = articlesPath;
    this.pagesPath = pagesPath;
    this.workflowsPath = workflowsPath;
    this.linksPath = linksPath;
    this.releasesPath = releasesPath;
    this.localePath = localePath;
  }

  public String getServicePath() {
    return servicePath;
  }
  public String getArticlesPath() {
    return articlesPath;
  }
  public String getPagesPath() {
    return pagesPath;
  }
  public String getWorkflowsPath() {
    return workflowsPath;
  }
  public String getLinksPath() {
    return linksPath;
  }
  public String getReleasesPath() {
    return releasesPath;
  }
  public String getLocalePath() {
    return localePath;
  }
  
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private String servicePath;
    private String articlesPath; 
    private String pagesPath;
    private String workflowsPath;
    private String linksPath;
    private String releasesPath;
    private String localePath;

    public Builder servicePath(String servicePath) {
      this.servicePath = servicePath;
      return this;
    }
    public Builder articlesPath(String articlesPath) {
      this.articlesPath = articlesPath;
      return this;
    }
    public Builder pagesPath(String pagesPath) {
      this.pagesPath = pagesPath;
      return this;
    }
    public Builder workflowsPath(String workflowsPath) {
      this.workflowsPath = workflowsPath;
      return this;
    }
    public Builder linksPath(String linksPath) {
      this.linksPath = linksPath;
      return this;
    }
    public Builder releasesPath(String releasesPath) {
      this.releasesPath = releasesPath;
      return this;
    }
    public Builder localePath(String localePath) {
      this.localePath = localePath;
      return this;
    }
    
    public ServicesPathConfig build() {
      return new ServicesPathConfig(servicePath, articlesPath, pagesPath, workflowsPath, linksPath, releasesPath, localePath);
    }
  }
}
