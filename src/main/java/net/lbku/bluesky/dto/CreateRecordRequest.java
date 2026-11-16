package net.lbku.bluesky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.Objects;

@NullMarked
public record CreateRecordRequest(
    String repo,
    String collection,
    Record record
) {
    public CreateRecordRequest {
        Objects.requireNonNull(repo);
        Objects.requireNonNull(collection);
        Objects.requireNonNull(record);
    }

    public record Record(
        @JsonProperty("$type") String type,
        String text,
        Instant createdAt,
        Embed embed
    ) {
        public Record {
            Objects.requireNonNull(type);
            Objects.requireNonNull(text);
            Objects.requireNonNull(createdAt);
            Objects.requireNonNull(embed);
        }
    }

    public record Embed(
        @JsonProperty("$type") String type,
        ExternalLink external
    ) {
        public Embed {
            Objects.requireNonNull(type);
            Objects.requireNonNull(external);
        }
    }
}
