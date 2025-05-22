package net.lbku.service;

import io.avaje.config.Config;
import io.avaje.jsonb.Jsonb;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import net.lbku.dto.Secret;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Objects;

@Getter
@Singleton
public final class SecretService {
    private final Secret secret;

    @Inject
    public SecretService(SecretsManagerClient secretsManagerClient, Jsonb jsonb) {
        Objects.requireNonNull(secretsManagerClient);

        Objects.requireNonNull(jsonb);

        this.secret = readSecrets(secretsManagerClient, jsonb);
    }

    private static Secret readSecrets(SecretsManagerClient secretsManagerClient, Jsonb jsonb) {
        String secretId = Config.get("app.secrets-manager.id");

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                                                             .secretId(secretId)
                                                             .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);

        String secretString = response.secretString();

        return jsonb.type(Secret.class)
                    .fromJson(secretString);
    }
}
