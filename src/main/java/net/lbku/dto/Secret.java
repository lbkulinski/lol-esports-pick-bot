package net.lbku.dto;

import io.avaje.jsonb.Json;

@Json
public record Secret(
    TwitterSecret twitter
) {
    public record TwitterSecret(
        String consumerKey,

        String consumerSecret,

        String accessToken,

        String accessSecret
    ) {}
}
