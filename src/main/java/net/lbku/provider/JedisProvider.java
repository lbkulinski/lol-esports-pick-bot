package net.lbku.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.lbku.service.SecretService;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

import java.util.Objects;

public final class JedisProvider implements Provider<UnifiedJedis> {
    private final SecretService secretService;

    @Inject
    public JedisProvider(SecretService secretService) {
        this.secretService = Objects.requireNonNull(secretService);
    }

    @Override
    public UnifiedJedis get() {
        String user = this.secretService.getSecret("REDIS_USER");

        String password = this.secretService.getSecret("REDIS_PASSWORD");

        String host = this.secretService.getSecret("REDIS_HOST");

        String portString = this.secretService.getSecret("REDIS_PORT");

        int port = Integer.parseInt(portString);

        HostAndPort hostAndPort = new HostAndPort(host, port);

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                                                           .user(user)
                                                           .password(password)
                                                           .build();

        return new UnifiedJedis(hostAndPort, config);
    }
}
