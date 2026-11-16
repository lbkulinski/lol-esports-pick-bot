package net.lbku.aws.config;

import com.rollbar.notifier.Rollbar;
import net.lbku.post.service.PostService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class FunctionConfig {
    @Bean
    public Supplier<String> gamePoster(
        PostService postService,
        Rollbar rollbar
    ) {
        return () -> {
            try {
                postService.postNewGames();
            } catch (Exception e) {
                var message = "An error occurred while posting new games";

                rollbar.error(e, message);

                throw e;
            }

            return "OK";
        };
    }
}
