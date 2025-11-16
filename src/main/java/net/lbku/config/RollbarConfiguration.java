package net.lbku.config;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.notifier.config.ConfigBuilder;
import net.lbku.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RollbarConfiguration {
    private final SecretService secretService;

    @Autowired
    public RollbarConfiguration(SecretService secretService) {
        this.secretService = secretService;
    }

    @Bean
    public Rollbar rollbar() {
        String accessToken = this.secretService.getSecret()
                                               .rollbar()
                                               .accessToken();

        Config config = ConfigBuilder.withAccessToken(accessToken)
                                     .build();

        Rollbar rollbar = Rollbar.init(config);

        rollbar.log("Hello, Rollbar");

        return rollbar;
    }
}
