package net.lbku.dto;

import io.avaje.jsonb.Json;

import java.util.List;

@Json
public record GameResponse(
    @Json.Property("cargoquery")
    List<GameWrapper> gameWrappers
) {
}
