package net.lbku.mediawiki.dto;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record GetTokenResponse(
    String batchcomplete,
    Query query
) {
    public GetTokenResponse {
        Objects.requireNonNull(batchcomplete, "batchcomplete must not be null");
        Objects.requireNonNull(query, "query must not be null");
    }

    public record Query(Tokens tokens) {
        public Query {
            Objects.requireNonNull(tokens, "tokens must not be null");
        }

        public record Tokens(String logintoken) {
            public Tokens {
                Objects.requireNonNull(logintoken, "logintoken must not be null");
            }
        }
    }
}
