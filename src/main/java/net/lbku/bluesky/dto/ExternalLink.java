package net.lbku.bluesky.dto;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record ExternalLink(
    String uri,
    String title,
    String description
) {
    public ExternalLink {
        Objects.requireNonNull(uri);
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
    }
}
