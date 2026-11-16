package net.lbku.bluesky.client;

import net.lbku.bluesky.dto.CreateRecordRequest;
import net.lbku.bluesky.dto.CreateSessionRequest;
import net.lbku.bluesky.dto.ExternalLink;
import net.lbku.bluesky.dto.Record;
import net.lbku.bluesky.dto.Session;
import net.lbku.bluesky.exception.BlueskyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

@Component
public final class BlueskyClient {
    private static final String SCHEME = "https";
    private static final String HOST = "bsky.social";

    private static final String CREATE_SESSION_ENDPOINT = "/xrpc/com.atproto.server.createSession";
    private static final String CREATE_RECORD_ENDPOINT = "/xrpc/com.atproto.repo.createRecord";

    private static final URI CREATE_SESSION_URI;
    private static final URI CREATE_RECORD_URI;

    private static final String AUTH_HEADER_TEMPLATE = "Bearer %s";

    private static final String EMBED_TYPE = "app.bsky.embed.external";
    private static final String RECORD_TYPE = "app.bsky.feed.post";

    static {
        CREATE_SESSION_URI = UriComponentsBuilder.newInstance()
                                                 .scheme(SCHEME)
                                                 .host(HOST)
                                                 .path(CREATE_SESSION_ENDPOINT)
                                                 .build()
                                                 .toUri();

        CREATE_RECORD_URI = UriComponentsBuilder.newInstance()
                                                .scheme(SCHEME)
                                                .host(HOST)
                                                .path(CREATE_RECORD_ENDPOINT)
                                                .build()
                                                .toUri();
    }

    private final RestClient restClient;

    @Autowired
    public BlueskyClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public Session createSession(String identifier, String password) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(password);

        var request = new CreateSessionRequest(identifier, password);

        Session session;

        try {
            session = this.restClient.post()
                                     .uri(CREATE_SESSION_URI)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(request)
                                     .retrieve()
                                     .toEntity(Session.class)
                                     .getBody();
        } catch (RestClientException e) {
            throw new BlueskyException("Failed to create session", e);
        }

        if (session == null) {
            throw new BlueskyException("Failed to create session");
        }

        return session;
    }

    public Record createRecord(Session session, String text, ExternalLink link) {
        Objects.requireNonNull(session);
        Objects.requireNonNull(text);
        Objects.requireNonNull(link);

        var authHeader = AUTH_HEADER_TEMPLATE.formatted(session.accessJwt());

        var embed = new CreateRecordRequest.Embed(EMBED_TYPE, link);

        var requestRecord = new CreateRecordRequest.Record(RECORD_TYPE, text, Instant.now(), embed);

        var request = new CreateRecordRequest(session.did(), RECORD_TYPE, requestRecord);

        Record record;

        try {
            record = this.restClient.post()
                                    .uri(CREATE_RECORD_URI)
                                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(request)
                                    .retrieve()
                                    .toEntity(Record.class)
                                    .getBody();
        } catch (RestClientException e) {
            throw new BlueskyException("Failed to create record", e);
        }

        if (record == null) {
            throw new BlueskyException("Failed to create record");
        }

        return record;
    }
}
