package net.lbku.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import net.lbku.provider.ObjectMapperProvider;

public final class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(ObjectMapper.class)
            .toProvider(ObjectMapperProvider.class);
    }
}
