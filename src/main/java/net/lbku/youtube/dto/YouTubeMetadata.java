package net.lbku.youtube.dto;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

@NullMarked
public record YouTubeMetadata(
    String title,

    @Nullable
    String description,

    URI imageUri
) {
    public YouTubeMetadata {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(imageUri, "imageUri must not be null");
    }
}
