package net.lbku.dto;

import io.avaje.jsonb.Json;

@Json
public record GameWrapper(@Json.Property("title") Game game) {
}
