package net.lbku.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.lbku.config.BotConfiguration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

import java.util.Objects;

public final class JedisProvider implements Provider<UnifiedJedis> {
    private final BotConfiguration configuration;

    @Inject
    public JedisProvider(BotConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    @Override
    public UnifiedJedis get() {
        String user = this.configuration.redisUser();

        String password = this.configuration.redisPassword();

        String host = this.configuration.redisHost();

        int port = this.configuration.redisPort();

        HostAndPort hostAndPort = new HostAndPort(host, port);

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                                                           .user(user)
                                                           .password(password)
                                                           .build();

        return new UnifiedJedis(hostAndPort, config);
    }
}
