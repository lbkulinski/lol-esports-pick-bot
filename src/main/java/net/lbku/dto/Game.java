package net.lbku.dto;

import io.avaje.jsonb.Json;
import net.lbku.dto.serializer.BooleanSerializer;
import net.lbku.dto.serializer.InstantSerializer;

import java.time.Instant;

@Json
public record Game(
    @Json.Property("GameId")
    String id,

    @Json.Property("Link")
    String player,

    @Json.Property("Tournament")
    String tournament,

    @Json.Property("DateTime UTC")
    @Json.Serializer(InstantSerializer.class)
    Instant timestamp,

    @Json.Property("PlayerWin")
    @Json.Serializer(BooleanSerializer.class)
    boolean won,

    @Json.Property("VOD")
    String vod
) {}
