package net.lbku.service;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import net.lbku.dto.Secret;
import net.lbku.exception.TwitterServiceException;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public final class TwitterService {
    private static final Logger log = LoggerFactory.getLogger(TwitterService.class);

    private static final String TWITTER_AUTHORIZATION_BASE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String TWITTER_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
    private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String TWITTER_API_URL = "https://api.twitter.com/2/tweets";
    private static final String CREATE_TWEET_REQUEST_TEMPLATE = """
    { "text": "%s" }""";

    private final SecretService secretService;

    @Autowired
    public TwitterService(SecretService secretService) {
        this.secretService = secretService;
    }

    public void postTweet(String text) {
        Secret.TwitterSecret secret = this.secretService.getSecret()
                                                        .twitter();

        OAuth1AccessToken accessToken = new OAuth1AccessToken(secret.accessToken(), secret.accessSecret());

        OAuthRequest request = new OAuthRequest(Verb.POST, TWITTER_API_URL);

        request.addHeader("Content-Type", "application/json");

        String payload = String.format(CREATE_TWEET_REQUEST_TEMPLATE, text);

        request.setPayload(payload);

        Response response;

        try (OAuth10aService service = this.getService(secret.consumerKey(), secret.consumerSecret())) {
            service.signRequest(accessToken, request);

            response = service.execute(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new TwitterServiceException("Failed to post tweet to Twitter", e);
        }

        int code = response.getCode();

        if (code == HttpStatus.SC_CREATED) {
            return;
        }

        String message = String.format("Failed to post tweet to Twitter, response code: %d", code);

        throw new TwitterServiceException(message);
    }

    private OAuth10aService getService(String consumerKey, String consumerSecret) {
        Objects.requireNonNull(consumerKey);
        Objects.requireNonNull(consumerSecret);

        return new ServiceBuilder(consumerKey)
            .apiSecret(consumerSecret)
            .build(new DefaultApi10a() {
                @Override
                protected String getAuthorizationBaseUrl() {
                    return TWITTER_AUTHORIZATION_BASE_URL;
                }

                @Override
                public String getRequestTokenEndpoint() {
                    return TWITTER_REQUEST_TOKEN_URL;
                }

                @Override
                public String getAccessTokenEndpoint() {
                    return TWITTER_ACCESS_TOKEN_URL;
                }
            });
    }
}
