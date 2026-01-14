package net.lbku.mediawiki.dto;

public record GetTokenResponse(
    String batchcomplete,
    Query query
) {
    public record Query(Tokens tokens) {
    }

    public record Tokens(String logintoken) {
    }
}
