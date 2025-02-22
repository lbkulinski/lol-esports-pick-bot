package net.lbku.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.lbku.service.SecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class BlueskyClient {
    private final SecretService secretService;

    private final ObjectMapper mapper;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(BlueskyClient.class);
    }

    @Inject
    public BlueskyClient(SecretService secretService, ObjectMapper mapper) {
        this.secretService = Objects.requireNonNull(secretService);

        this.mapper = Objects.requireNonNull(mapper);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Session(String accessJwt, String refreshJwt) {}

    private Session createSession() {
        String uriString = "https://bsky.social/xrpc/com.atproto.server.createSession";

        URI uri = URI.create(uriString);

        String appPassword = this.secretService.getSecret("BLUESKY_APP_PASSWORD");

        Map<String, String> requestBody = Map.of(
            "identifier", "lol-vods.bsky.social",
            "password", appPassword
        );

        String requestBodyJson;

        try {
            requestBodyJson = this.mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            String message = e.getMessage();

            BlueskyClient.LOGGER.error(message, e);

            return null;
        }

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(requestBodyJson);

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(bodyPublisher)
                                         .header("Content-Type", "application/json")
                                         .header("Accept", "application/json")
                                         .build();

        HttpResponse<String> response;

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            String message = e.getMessage();

            BlueskyClient.LOGGER.error(message, e);

            return null;
        }

        String responseBody = response.body();

        Session session;

        try {
            session = this.mapper.readValue(responseBody, Session.class);
        } catch (JsonProcessingException e) {
            String message = e.getMessage();

            BlueskyClient.LOGGER.error(message, e);

            return null;
        }

        return session;
    }

    public enum PostStatus {
        SUCCESS,
        FAILURE,
    }

    public PostStatus createPost(String text) {
        Objects.requireNonNull(text);

        String uriString = "https://bsky.social/xrpc/com.atproto.repo.createRecord";

        URI uri = URI.create(uriString);

        Session session = this.createSession();

        if (session == null) {
            BlueskyClient.LOGGER.error("A session could not be created");

            return PostStatus.FAILURE;
        }

        String accessJwt = session.accessJwt();

        String authorizationHeader = "Bearer %s".formatted(accessJwt);

        Instant now = Instant.now();

        Map<String, ?> requestBody = Map.of(
            "repo", "lol-vods.bsky.social",
            "collection", "app.bsky.feed.post",
            "record", Map.of(
                "text", text,
                "createdAt", now
            )
        );

        String requestBodyJson;

        try {
            requestBodyJson = this.mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            String message = e.getMessage();

            BlueskyClient.LOGGER.error(message, e);

            return null;
        }

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(requestBodyJson);

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(bodyPublisher)
                                         .header("Authorization", authorizationHeader)
                                         .header("Content-Type", "application/json")
                                         .header("Accept", "application/json")
                                         .build();

        HttpResponse<String> response;

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            String message = e.getMessage();

            BlueskyClient.LOGGER.error(message, e);

            return null;
        }

        int responseCode = response.statusCode();

        int expectedStatusCode = 200;

        if (responseCode != expectedStatusCode) {
            BlueskyClient.LOGGER.error("Post creation failed with status code {}", responseCode);

            return PostStatus.FAILURE;
        }

        return PostStatus.SUCCESS;
    }
}
