package net.lbku.mediawiki.dto;

public record LoginResponse(LoginResult login) {
    public record LoginResult(
        String result,
        String lguserid,
        String lgusername
    ) {
    }
}
