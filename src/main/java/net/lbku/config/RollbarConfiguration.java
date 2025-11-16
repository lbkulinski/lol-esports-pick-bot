package net.lbku.config;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.notifier.config.ConfigBuilder;
import net.lbku.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RollbarConfiguration {
    private final SecretService secretService;
    private final String environment;
    private final String codeVersion;

    @Autowired
    public RollbarConfiguration(
        SecretService secretService,
        @Value("${app.rollbar.environment}" ) String environment,
        @Value("${app.rollbar.code-version}" ) String codeVersion
    ) {
        this.secretService = secretService;
        this.environment = environment;
        this.codeVersion = codeVersion;
    }

    @Bean
    public Rollbar rollbar() {
        String accessToken = this.secretService.getSecret()
                                               .rollbar()
                                               .accessToken();

        Config config = ConfigBuilder.withAccessToken(accessToken)
                                     .environment(this.environment)
                                     .codeVersion(this.codeVersion)
                                     .build();

        return Rollbar.init(config);
    }
}
