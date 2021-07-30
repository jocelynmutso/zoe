package io.github.jocelynmutso.zoe.persistence.spi.builders;

import io.github.jocelynmutso.zoe.persistence.api.UpdateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Article;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Entity;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Link;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Locale;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Page;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence.Workflow;
import io.github.jocelynmutso.zoe.persistence.spi.PersistenceConfig;

public class UpdateBuilderImpl implements UpdateBuilder {

 private final PersistenceConfig config;
  
  public UpdateBuilderImpl(PersistenceConfig config) {
    super();
    this.config = config;
  }

  @Override
  public Entity<Locale> locale(LocaleMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Article> article(ArticleMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Page> page(PageMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Link> link(LinkMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Entity<Workflow> workflow(WorkflowMutator changes) {
    // TODO Auto-generated method stub
    return null;
  }

}
