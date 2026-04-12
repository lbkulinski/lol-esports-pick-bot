package net.lbku.bluesky.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.lbku.bluesky.dto.BlueskyPost;
import net.lbku.bluesky.dto.BlueskySession;
import net.lbku.bluesky.exception.BlueskyException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Objects;

@Component
@NullMarked
public final class BlueskyClient {
    private static final Logger log = LoggerFactory.getLogger(BlueskyClient.class);

    private static final String SCHEME = "https";
    private static final String HOST = "bsky.social";

    private static final String CREATE_SESSION_ENDPOINT = "/xrpc/com.atproto.server.createSession";
    private static final URI CREATE_SESSION_URI;

    private static final String CREATE_RECORD_ENDPOINT = "/xrpc/com.atproto.repo.createRecord";
    private static final URI CREATE_RECORD_URI;

    private static final String FEED_POST_COLLECTION = "app.bsky.feed.post";

    static {
        try {
            CREATE_SESSION_URI = new URIBuilder()
                .setScheme(SCHEME)
                .setHost(HOST)
                .setPath(CREATE_SESSION_ENDPOINT)
                .build();

            CREATE_RECORD_URI = new URIBuilder()
                .setScheme(SCHEME)
                .setHost(HOST)
                .setPath(CREATE_RECORD_ENDPOINT)
                .build();
        } catch (URISyntaxException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BlueskyClient(
        CloseableHttpClient httpClient,
        ObjectMapper objectMapper
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public BlueskySession createSession(String handle, String password) {
        Objects.requireNonNull(handle, "handle must not be null");
        Objects.requireNonNull(password, "password must not be null");

        CreateSessionRequest requestPayload = new CreateSessionRequest(handle, password);

        String payload;

        try {
            payload = this.objectMapper.writeValueAsString(requestPayload);
        } catch (JacksonException e) {
            String message = "Failed to serialize create session request payload";

            throw new BlueskyException(message, e);
        }

        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(CREATE_SESSION_URI);

        httpPost.setEntity(entity);

        try {
            return this.httpClient.execute(httpPost, httpResponse -> {
                int statusCode = httpResponse.getStatusLine()
                                             .getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    String message = "Failed to create session with Bluesky, response code: %d".formatted(statusCode);

                    throw new BlueskyException(message);
                }

                String entityString = EntityUtils.toString(httpResponse.getEntity());

                log.info("Create session response: {}", entityString);

                CreateSessionResponse response = this.objectMapper.readValue(
                    entityString,
                    CreateSessionResponse.class
                );

                return new BlueskySession(handle, response.accessJwt(), response.refreshJwt());
            });
        } catch (IOException e) {
            String message = "Failed to create session with Bluesky";

            throw new BlueskyException(message, e);
        } catch (JacksonException e) {
            String message = "Failed to parse create session response JSON";

            throw new BlueskyException(message, e);
        }
    }

    public BlueskyPost post(BlueskySession session, String text) {
        Objects.requireNonNull(session, "session must not be null");
        Objects.requireNonNull(text, "text must not be null");

        CreateRecordRequest requestPayload = new CreateRecordRequest(
            session.handle(),
            FEED_POST_COLLECTION,
            new CreateRecordRequest.Record(text, Instant.now())
        );

        String payload;

        try {
            payload = this.objectMapper.writeValueAsString(requestPayload);
        } catch (JacksonException e) {
            String message = "Failed to serialize create record request payload";

            throw new BlueskyException(message, e);
        }

        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(CREATE_RECORD_URI);

        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(session.accessJwt()));

        httpPost.setEntity(entity);

        try {
            return this.httpClient.execute(httpPost, httpResponse -> {
                int statusCode = httpResponse.getStatusLine()
                                             .getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    String message = "Failed to create record with Bluesky, response code: %d".formatted(statusCode);

                    throw new BlueskyException(message);
                }

                String entityString = EntityUtils.toString(httpResponse.getEntity());

                log.info("Create record response: {}", entityString);

                return this.objectMapper.readValue(entityString, BlueskyPost.class);
            });
        } catch (IOException e) {
            String message = "Failed to create record with Bluesky";

            throw new BlueskyException(message, e);
        } catch (JacksonException e) {
            String message = "Failed to parse create record response JSON";

            throw new BlueskyException(message, e);
        }
    }

    private record CreateSessionRequest(
        String identifier,
        String password
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CreateSessionResponse(
        String accessJwt,
        String refreshJwt
    ) {
    }

    private record CreateRecordRequest(
        String repo,
        String collection,
        Record record
    ) {
        private record Record(
            String text,
            Instant createdAt
        ) {
        }
    }
}
