package net.lbku.config;

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

    @Autowired
    public FunctionConfiguration(PostService postService) {
        this.postService = postService;
    }

    @Bean
    public Supplier<String> gamePoster() {
        return () -> {
            postService.postNewGames();
            return "OK";
        };
    }
}
