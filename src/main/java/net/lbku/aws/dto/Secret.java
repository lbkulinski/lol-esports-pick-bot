package net.lbku.aws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record Secret(
    RollbarSecret rollbar,
    FandomSecret fandom,
    BlueskySecret bluesky
) {
    public Secret {
        Objects.requireNonNull(rollbar);
        Objects.requireNonNull(fandom);
        Objects.requireNonNull(bluesky);
    }

    public record RollbarSecret(String accessToken) {
        public RollbarSecret {
            Objects.requireNonNull(accessToken);
        }

        @Override
        public String toString() {
            return "RollbarSecret{accessToken=***}";
        }
    }

    public record FandomSecret(
        String username,
        String password
    ) {
        public FandomSecret {
            Objects.requireNonNull(username);
            Objects.requireNonNull(password);
        }

        @Override
        public String toString() {
            return "FandomSecret{username=***, password=***}";
        }
    }

    public record BlueskySecret(
        String identifier,
        String appPassword
    ) {
        public BlueskySecret {
            Objects.requireNonNull(identifier);
            Objects.requireNonNull(appPassword);
        }

        @Override
        public String toString() {
            return "BlueskySecret{identifier=***, appPassword=***}";
        }
    }
}
