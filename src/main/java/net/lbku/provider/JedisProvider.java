package net.lbku.provider;

import com.google.inject.Provider;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

public final class JedisProvider implements Provider<UnifiedJedis> {
    @Override
    public UnifiedJedis get() {
        String user = System.getenv("REDIS_USER");

        if (user == null) {
            throw new RuntimeException("the REDIS_USER environment variable is not set");
        }

        String password = System.getenv("REDIS_PASSWORD");

        if (password == null) {
            throw new RuntimeException("the REDIS_PASSWORD environment variable is not set");
        }

        String host = System.getenv("REDIS_HOST");

        if (host == null) {
            throw new RuntimeException("the REDIS_HOST environment variable is not set");
        }

        String portString = System.getenv("REDIS_PORT");

        if (portString == null) {
            throw new RuntimeException("the REDIS_PORT environment variable is not set");
        }

        int port = Integer.parseInt(portString);

        HostAndPort hostAndPort = new HostAndPort(host, port);

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                                                           .user(user)
                                                           .password(password)
                                                           .build();

        return new UnifiedJedis(hostAndPort, config);
    }
}
