package net.lbku.mediawiki.client;

import net.lbku.aws.client.AwsSecretsClient;
import net.lbku.aws.dto.Secret;
import net.lbku.mediawiki.dto.GetTokenResponse;
import net.lbku.mediawiki.dto.LoginResponse;
import net.lbku.mediawiki.exception.MediaWikiException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
public final class MediaWikiClient {
    private static final String SCHEMA = "https";
    private static final String HOST = "lol.fandom.com";
    private static final String API_PATH = "api.php";
    private static final String LOGIN_SUCCESS = "Success";

    private final AwsSecretsClient awsSecretsClient;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public MediaWikiClient(
        AwsSecretsClient awsSecretsClient,
        CloseableHttpClient httpClient,
        ObjectMapper objectMapper
    ) {
        this.awsSecretsClient = awsSecretsClient;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String getLoginToken() {
        HttpPost httpPost = this.buildGetTokenRequest();

        String token;

        try {
            token = this.httpClient.execute(httpPost, this::handleGetTokenResponse);
        } catch (IOException e) {
            String message = "Failed to get login token";

            throw new MediaWikiException(message, e);
        }

        return token;
    }

    public void login(String loginToken) {
        if (loginToken == null) {
            throw new IllegalArgumentException("loginToken must not be null");
        }

        HttpPost httpPost = this.buildLoginRequest(loginToken);

        try {
            this.httpClient.execute(httpPost, this::handleLoginResponse);
        } catch (IOException e) {
            String message = "Failed to login to MediaWiki";

            throw new MediaWikiException(message, e);
        }
    }

    private URI buildUri() {
        URI uri;

        try {
            uri = new URIBuilder()
                .setScheme(SCHEMA)
                .setHost(HOST)
                .setPath(API_PATH)
                .build();
        } catch (URISyntaxException e) {
            String message = "Failed to build URI";

            throw new MediaWikiException(message, e);
        }

        return uri;
    }

    private HttpPost buildGetTokenRequest() {
        URI uri = this.buildUri();

        HttpEntity entity = new UrlEncodedFormEntity(
            List.of(
                new BasicNameValuePair("action", "query"),
                new BasicNameValuePair("meta", "tokens"),
                new BasicNameValuePair("type", "login"),
                new BasicNameValuePair("format", "json")
            ),
            StandardCharsets.UTF_8
        );

        HttpPost httpPost = new HttpPost(uri);

        httpPost.setEntity(entity);

        return httpPost;
    }

    private HttpPost buildLoginRequest(String loginToken) {
        URI uri = this.buildUri();

        Secret.FandomSecret fandomSecret = this.awsSecretsClient.getAppSecret()
                                                                .fandom();

        String username = fandomSecret.username();
        String password = fandomSecret.password();

        HttpEntity entity = new UrlEncodedFormEntity(
            List.of(
                new BasicNameValuePair("action", "login"),
                new BasicNameValuePair("lgname", username),
                new BasicNameValuePair("lgpassword", password),
                new BasicNameValuePair("lgtoken", loginToken),
                new BasicNameValuePair("format", "json")
            ),
            StandardCharsets.UTF_8
        );

        HttpPost httpPost = new HttpPost(uri);

        httpPost.setEntity(entity);

        return httpPost;
    }

    private String handleGetTokenResponse(HttpResponse httpResponse) {
        String entityString = this.extractEntityString(httpResponse);

        GetTokenResponse response;

        try {
            response = this.objectMapper.readValue(entityString, GetTokenResponse.class);
        } catch (JacksonException e) {
            String message = "Failed to parse response JSON";

            throw new MediaWikiException(message, e);
        }

        return response.query()
                       .tokens()
                       .logintoken();
    }

    private Void handleLoginResponse(HttpResponse httpResponse) {
        String entityString = this.extractEntityString(httpResponse);

        LoginResponse response;

        try {
            response = this.objectMapper.readValue(entityString, LoginResponse.class);
        } catch (JacksonException e) {
            String message = "Failed to parse response JSON";

            throw new MediaWikiException(message, e);
        }

        String result = response.login()
                                .result();

        if (!Objects.equals(result, LOGIN_SUCCESS)) {
            String message = String.format("Login failed: %s", result);

            throw new MediaWikiException(message);
        }

        return null;
    }

    private String extractEntityString(HttpResponse httpResponse) {
        int statusCode = httpResponse.getStatusLine()
                                     .getStatusCode();

        if (statusCode < 200 || statusCode >= 300) {
            throw new MediaWikiException("Unexpected response status: " + statusCode);
        }

        HttpEntity entity = httpResponse.getEntity();

        String entityString;

        try {
            entityString = EntityUtils.toString(entity);
        } catch (IOException e) {
            String message = "Failed to read response entity";

            throw new MediaWikiException(message, e);
        }

        return entityString;
    }
}
