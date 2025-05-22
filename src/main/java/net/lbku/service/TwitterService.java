package net.lbku.service;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Singleton
public final class TwitterService {
    private final SecretService secretService;

    private static final String TWITTER_API_URL;

    private static final Logger LOGGER;

    static {
        TWITTER_API_URL = "https://api.twitter.com/2/tweets";

        LOGGER = LoggerFactory.getLogger(TwitterService.class);
    }

    @Inject
    public TwitterService(SecretService secretService) {
        this.secretService = Objects.requireNonNull(secretService);
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
        String token = this.secretService.getSecret()
                                         .twitter()
                                         .accessToken();

        String tokenSecret = this.secretService.getSecret()
                                               .twitter()
                                               .accessSecret();

        OAuth1AccessToken accessToken = new OAuth1AccessToken(token, tokenSecret);

        OAuthRequest request = new OAuthRequest(Verb.POST, TWITTER_API_URL);

        request.addHeader("Content-Type", "application/json");

        request.setPayload("""
        {
            "text": "%s"
        }""".formatted(text));

        String consumerKey = this.secretService.getSecret()
                                               .twitter()
                                               .consumerKey();

        String consumerSecret = this.secretService.getSecret()
                                                  .twitter()
                                                  .consumerSecret();

        Response response;

        try (OAuth10aService service = this.getService(consumerKey, consumerSecret)) {
            service.signRequest(accessToken, request);

            response = service.execute(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            String message = e.getMessage();

            LOGGER.error(message, e);

            return TweetStatus.FAILURE;
        }

        int code = response.getCode();

        int expectedCode = 201;

        if (code == expectedCode) {
            return TweetStatus.SUCCESS;
        }

        LOGGER.error("Unexpected response code received from Twitter: {}", code);

        return TweetStatus.FAILURE;
    }
}
