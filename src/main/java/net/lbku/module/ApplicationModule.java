package net.lbku.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import net.lbku.config.BotConfiguration;
import net.lbku.provider.BotConfigurationProvider;
import net.lbku.provider.JedisProvider;
import net.lbku.provider.ObjectMapperProvider;
import redis.clients.jedis.UnifiedJedis;

public final class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(BotConfiguration.class)
            .toProvider(BotConfigurationProvider.class);

        this.bind(ObjectMapper.class)
            .toProvider(ObjectMapperProvider.class);

        this.bind(UnifiedJedis.class)
            .toProvider(JedisProvider.class);
    }
}
