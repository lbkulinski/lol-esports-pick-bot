package net.lbku.service;

import com.amazonaws.secretsmanager.caching.SecretCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.Map;
import java.util.Objects;

public final class SecretService {
    private final SecretCache secretCache;

    private final ObjectMapper mapper;

    @Inject
    public SecretService(SecretCache secretCache, ObjectMapper mapper) {
        this.secretCache = Objects.requireNonNull(secretCache);

        this.mapper = Objects.requireNonNull(mapper);
    }

    public String getSecret(String id) {
        Objects.requireNonNull(id);

        String secretId = System.getenv("SECRET_ID");

        if (secretId == null) {
            throw new IllegalStateException("SECRET_ID is not set");
        }

        String secret = this.secretCache.getSecretString(secretId);

        TypeReference<Map<String, String>> typeReference = new TypeReference<>() {};

        Map<String, String> secretMap;

        try {
            secretMap = this.mapper.readValue(secret, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String value = secretMap.get(id);

        if (value == null) {
            throw new IllegalStateException("Secret ID %s not found".formatted(id));
        }

        return value;
    }
}
