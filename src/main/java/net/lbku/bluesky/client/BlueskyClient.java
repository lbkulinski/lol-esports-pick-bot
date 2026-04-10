package net.lbku.bluesky.client;

import net.lbku.aws.client.AwsSecretsClient;
import net.lbku.aws.dto.Secret;
import net.lbku.bluesky.dto.BlueskySession;
import net.lbku.bluesky.exception.TwitterServiceException;
import org.apache.http.HttpStatus;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@NullMarked
public final class BlueskyClient {
    private static final String TWITTER_AUTHORIZATION_BASE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String TWITTER_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
    private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String TWITTER_API_URL = "https://api.twitter.com/2/tweets";
    private static final String CREATE_TWEET_REQUEST_TEMPLATE = """
    { "text": "%s" }""";

    private final AwsSecretsClient awsSecretsClient;

    @Autowired
    public BlueskyClient(AwsSecretsClient awsSecretsClient) {
        this.awsSecretsClient = awsSecretsClient;
    }

    public void post(String text) {
//        if (text == null) {
//            throw new IllegalArgumentException("text must not be null");
//        }
//
//        Secret.TwitterSecret secret = this.awsSecretsClient.getAppSecret()
//                                                           .twitter();
//
//        OAuth1AccessToken accessToken = new OAuth1AccessToken(secret.accessToken(), secret.accessSecret());
//
//        OAuthRequest request = new OAuthRequest(Verb.POST, TWITTER_API_URL);
//
//        request.addHeader("Content-Type", "application/json");
//
//        String payload = String.format(CREATE_TWEET_REQUEST_TEMPLATE, text);
//
//        request.setPayload(payload);
//
//        Response response;
//
//        try (OAuth10aService service = this.getService(secret.consumerKey(), secret.consumerSecret())) {
//            service.signRequest(accessToken, request);
//
//            response = service.execute(request);
//        } catch (IOException | ExecutionException | InterruptedException e) {
//            throw new TwitterServiceException("Failed to post tweet to Twitter", e);
//        }
//
//        int code = response.getCode();
//
//        if (code == HttpStatus.SC_CREATED) {
//            return;
//        }
//
//        String message = String.format("Failed to post tweet to Twitter, response code: %d", code);
//
//        throw new TwitterServiceException(message);
    }

    /*
    curl -X POST $PDSHOST/xrpc/com.atproto.server.createSession \
    -H "Content-Type: application/json" \
    -d '{"identifier": "'"$BLUESKY_HANDLE"'", "password": "'"$BLUESKY_PASSWORD"'"}'
     */

    public BlueskySession createSession(String handle, String password) {
        Objects.requireNonNull(handle, "handle must not be null");
        Objects.requireNonNull(password, "password must not be null");

        return new BlueskySession("accessJwt", "refreshJwt");
    }
}
