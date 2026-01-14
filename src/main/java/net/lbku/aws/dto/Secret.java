package net.lbku.aws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Secret(
    RollbarSecret rollbar,
    FandomSecret fandom,
    TwitterSecret twitter
) {
    public record RollbarSecret(String accessToken) {
    }

    public record FandomSecret(
        String username,
        String password
    ) {
    }

    public record TwitterSecret(
        String consumerKey,
        String consumerSecret,
        String accessToken,
        String accessSecret
    ) {
    }
}
