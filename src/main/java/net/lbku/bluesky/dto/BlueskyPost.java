package net.lbku.bluesky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record BlueskyPost(
    String uri,
    String cid
) {
    public BlueskyPost {
        Objects.requireNonNull(uri, "uri must not be null");
        Objects.requireNonNull(cid, "cid must not be null");
    }
}
