package net.lbku.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.lbku.dto.Secret;
import net.lbku.exception.SecretServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Service
public final class SecretService {
    @Getter
    private final Secret secret;

    @Autowired
    public SecretService(
        @Value("${app.aws.secrets-manager.id}") String secretId,
        SecretsManagerClient secretsManagerClient,
        ObjectMapper objectMapper
    ) {
        this.secret = readSecrets(secretId, secretsManagerClient, objectMapper);
    }

    private static Secret readSecrets(
        String secretId,
        SecretsManagerClient secretsManagerClient,
        ObjectMapper objectMapper
    ) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                                                             .secretId(secretId)
                                                             .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);

        String secretString = response.secretString();

        Secret secret;

        try {
            secret = objectMapper.readValue(secretString, Secret.class);
        } catch (JsonProcessingException e) {
            throw new SecretServiceException("Failed to parse secrets from AWS Secrets Manager");
        }

        return secret;
    }
}
