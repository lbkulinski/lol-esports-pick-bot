package net.lbku.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Secret(
    RollbarSecret rollbar,
    TwitterSecret twitter
) {
    public record RollbarSecret(String accessToken) {
    }

    public record TwitterSecret(
        String consumerKey,

        String consumerSecret,

        String accessToken,

        String accessSecret
    ) {
    }
}
