package io.github.jocelynmutso.zoe.persistence.spi;

import java.util.function.Consumer;

import io.github.jocelynmutso.zoe.persistence.api.CreateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.DeleteBuilder;
import io.github.jocelynmutso.zoe.persistence.api.UpdateBuilder;
import io.github.jocelynmutso.zoe.persistence.api.ZoePersistence;
import io.github.jocelynmutso.zoe.persistence.spi.builders.CreateBuilderImpl;
import io.github.jocelynmutso.zoe.persistence.spi.builders.DeleteBuilderImpl;
import io.github.jocelynmutso.zoe.persistence.spi.builders.UpdateBuilderImpl;

public class ZoePersistenceImpl implements ZoePersistence {

  private final PersistenceConfig config;
  
  public ZoePersistenceImpl(PersistenceConfig config) {
    super();
    this.config = config;
  }

  @Override
  public CreateBuilder create() {
    return new CreateBuilderImpl(config);
  }

  @Override
  public UpdateBuilder update() {
    return new UpdateBuilderImpl(config);
  }

  @Override
  public DeleteBuilder delete() {
    return new DeleteBuilderImpl(config);
  }

  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private ImmutablePersistenceConfig.Builder config = ImmutablePersistenceConfig.builder();
    
    public Builder config(Consumer<ImmutablePersistenceConfig.Builder> config) {
      config.accept(this.config);
      return this;
    }
    public ZoePersistenceImpl build() {
      return new ZoePersistenceImpl(config.build());
    }
  }
}
