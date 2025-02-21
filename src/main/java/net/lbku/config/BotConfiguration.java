package net.lbku.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:application.properties")
public interface BotConfiguration extends Config {
    @Key("redis.user")
    String redisUser();

    @Key("redis.password")
    String redisPassword();

    @Key("redis.host")
    String redisHost();

    @Key("redis.port")
    int redisPort();

    @Key("twitter.consumer_key")
    String twitterConsumerKey();

    @Key("twitter.consumer_secret")
    String twitterConsumerSecret();

    @Key("twitter.access_token")
    String twitterAccessToken();

    @Key("twitter.access_secret")
    String twitterAccessSecret();
}
