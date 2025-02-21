package net.lbku.module;

import com.amazonaws.secretsmanager.caching.SecretCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import net.lbku.provider.JedisProvider;
import net.lbku.provider.ObjectMapperProvider;
import net.lbku.provider.SecretCacheProvider;
import redis.clients.jedis.UnifiedJedis;

public final class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(ObjectMapper.class)
            .toProvider(ObjectMapperProvider.class);

        this.bind(SecretCache.class)
            .toProvider(SecretCacheProvider.class);

        this.bind(UnifiedJedis.class)
            .toProvider(JedisProvider.class);
    }
}
