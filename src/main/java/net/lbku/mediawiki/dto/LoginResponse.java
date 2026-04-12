package net.lbku.mediawiki.dto;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record LoginResponse(LoginResult login) {
    public LoginResponse {
        Objects.requireNonNull(login, "login must not be null");
    }

    public record LoginResult(
        String result,
        String lguserid,
        String lgusername
    ) {
        public LoginResult {
            Objects.requireNonNull(result, "result must not be null");
            Objects.requireNonNull(lguserid, "lguserid must not be null");
            Objects.requireNonNull(lgusername, "lgusername must not be null");
        }
    }
}
