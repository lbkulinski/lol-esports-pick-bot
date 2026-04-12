package net.lbku.youtube.client;

import net.lbku.youtube.dto.YouTubeMetadata;
import net.lbku.youtube.exception.YouTubeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Component
public final class YouTubeMetadataClient {
    public YouTubeMetadata getMetadata(URI videoUri) {
        Objects.requireNonNull(videoUri, "videoUri must not be null");

        Document document;

        try {
            document = Jsoup.connect(videoUri.toString())
                            .get();
        } catch (IOException e) {
            String message = "Failed to fetch YouTube video page for URI: %s".formatted(videoUri);

            throw new YouTubeException(message, e);
        }

        Element titleElement = document.selectFirst("meta[name=title]");

        if (titleElement == null) {
            String message = "Failed to find title element in YouTube video page for URI: %s".formatted(videoUri);

            throw new YouTubeException(message);
        }

        String title = titleElement.attr("content");

        Element descriptionElement = document.selectFirst("meta[name=description]");

        String description = null;

        if (descriptionElement != null) {
            description = descriptionElement.attr("content");
        }

        Element imageElement = document.selectFirst("meta[property=og:image]");

        if (imageElement == null) {
            String message = "Failed to find image element in YouTube video page for URI: %s".formatted(videoUri);

            throw new YouTubeException(message);
        }

        String imageUriString = imageElement.attr("content");

        URI imageUri;

        try {
            imageUri = URI.create(imageUriString);
        } catch (IllegalArgumentException e) {
            String message = "Failed to parse image URI from YouTube video page for URI: %s".formatted(videoUri);

            throw new YouTubeException(message, e);
        }

        return new YouTubeMetadata(title, description, imageUri);
    }
}
