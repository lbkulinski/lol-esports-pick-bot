package net.lbku.config;

import com.rollbar.notifier.Rollbar;
import net.lbku.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class FunctionConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FunctionConfiguration.class);

    private final PostService postService;
    private final Rollbar rollbar;

    @Autowired
    public FunctionConfiguration(
        PostService postService,
        Rollbar rollbar
    ) {
        this.postService = postService;
        this.rollbar = rollbar;
    }

    @Bean
    public Supplier<String> gamePoster() {
        return () -> {
            try {
                this.postService.postNewGames();
            } catch (Exception e) {
                String message = "An error occurred while posting new games";

                log.error(message, e);

                this.rollbar.error(e, message);

                return "ERROR";
            }

            return "OK";
        };
    }
}
