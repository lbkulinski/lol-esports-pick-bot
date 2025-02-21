package net.lbku.client;


import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.inject.Inject;
import net.lbku.config.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public final class TwitterClient {
    private final BotConfiguration configuration;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(TwitterClient.class);
    }

    @Inject
    public TwitterClient(BotConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    private OAuth10aService getService(String consumerKey, String consumerSecret) {
        Objects.requireNonNull(consumerKey);

        Objects.requireNonNull(consumerSecret);

        return new ServiceBuilder(consumerKey)
            .apiSecret(consumerSecret)
            .build(new DefaultApi10a() {
                @Override
                protected String getAuthorizationBaseUrl() {
                    return "https://api.twitter.com/oauth/authorize";
                }

                @Override
                public String getRequestTokenEndpoint() {
                    return "https://api.twitter.com/oauth/request_token";
                }

                @Override
                public String getAccessTokenEndpoint() {
                    return "https://api.twitter.com/oauth/access_token";
                }
            });
    }

    public enum TweetStatus {
        SUCCESS,
        FAILURE,
    }

    public TweetStatus postTweet(String text) {
        String token = this.configuration.twitterAccessToken();

        String tokenSecret = this.configuration.twitterAccessSecret();

        OAuth1AccessToken accessToken = new OAuth1AccessToken(token, tokenSecret);

        String url = "https://api.twitter.com/2/tweets";

        OAuthRequest request = new OAuthRequest(Verb.POST, url);

        request.addHeader("Content-Type", "application/json");

        request.setPayload("""
        {
            "text": "%s"
        }""".formatted(text));

        String consumerKey = this.configuration.twitterConsumerKey();

        String consumerSecret = this.configuration.twitterConsumerSecret();

        Response response;

        try (OAuth10aService service = this.getService(consumerKey, consumerSecret)) {
            service.signRequest(accessToken, request);

            response = service.execute(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            String message = e.getMessage();

            TwitterClient.LOGGER.error(message, e);

            return TweetStatus.FAILURE;
        }

        int code = response.getCode();

        int expectedCode = 201;

        if (code == expectedCode) {
            return TweetStatus.SUCCESS;
        }

        return TweetStatus.FAILURE;
    }
}
