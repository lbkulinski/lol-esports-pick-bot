package net.lbku.aws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record Secret(
    RollbarSecret rollbar,
    FandomSecret fandom,
    BlueskySecret bluesky
) {
    public record RollbarSecret(String accessToken) {
    }

    public record FandomSecret(
        String username,
        String password
    ) {
    }

    public record BlueskySecret(
        String appPassword
    ) {
    }

    @Override
    public String toString() {
        return """
        Secret{
            rollbar=RollbarSecret{accessToken=REDACTED},
            fandom=FandomSecret{username=REDACTED, password=REDACTED},
            bluesky=BlueskySecret{appPassword=REDACTED}
        }""";
    }
}
