package net.lbku.aws.config;

import com.rollbar.notifier.Rollbar;
import net.lbku.social.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class FunctionConfig {
    private final PostService postService;
    private final Rollbar rollbar;

    @Autowired
    public FunctionConfig(
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

                this.rollbar.error(e, message);

                throw e;
            }

            return "OK";
        };
    }
}
