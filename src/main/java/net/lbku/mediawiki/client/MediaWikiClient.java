package net.lbku.mediawiki.client;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public final class MediaWikiClient {
    private static final String SCHEMA = "https";
    private static final String HOST = "lol.fandom.com";
    private static final String API_PATH = "api.php";

    private final CloseableHttpClient httpClient;

    @Autowired
    public MediaWikiClient(
        CloseableHttpClient httpClient
    ) {
        this.httpClient = httpClient;
    }

    public String getToken() {
        /*
        API_URL="api.php"
RESULT=$(curl -fsSL -X POST \
 -d action=query \
 -d meta=tokens \
 -d type=login \
 -d format=json  \
 -c cookie.txt \
 -b cookie.txt \
 "${MW_URL}${API_URL}")
RESULT=${RESULT/*token\":\"}
TOKEN=${RESULT%\\\"*}
         */

        URI uri;

        try {
            uri = new URIBuilder()
                .setScheme(SCHEMA)
                .setHost(HOST)
                .setPath(API_PATH)
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
