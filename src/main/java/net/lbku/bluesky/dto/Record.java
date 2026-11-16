package net.lbku.bluesky.dto;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record Record(
    String cid,
    String uri
) {
    public Record {
        Objects.requireNonNull(cid);
        Objects.requireNonNull(uri);
    }
}
