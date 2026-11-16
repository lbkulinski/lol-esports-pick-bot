package net.lbku.aws.client;

import com.amazonaws.secretsmanager.caching.SecretCache;
import net.lbku.aws.dto.Secret;
import net.lbku.aws.exception.SecretsClientException;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
@NullMarked
public final class SecretsClient {
    private final SecretCache secretCache;
    private final JsonMapper jsonMapper;

    private final String appSecretId;

    @Autowired
    public SecretsClient(
        SecretsManagerClient secretsManagerClient,
        JsonMapper jsonMapper,
        @Value("${app.aws.secrets-manager.id}") String appSecretId
    ) {
        this.secretCache = new SecretCache(secretsManagerClient);
        this.jsonMapper = jsonMapper;
        this.appSecretId = appSecretId;
    }

    public Secret getAppSecret() {
        String secretString = this.secretCache.getSecretString(this.appSecretId);

        try {
            return this.jsonMapper.readValue(secretString, Secret.class);
        } catch (JacksonException e) {
            throw new SecretsClientException("Failed to parse main application secret JSON", e);
        }
    }
}
