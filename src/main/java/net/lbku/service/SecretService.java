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

    public String getSecret(String name) {
        Objects.requireNonNull(name);

        String secretName = System.getenv("SECRET_NAME");

        if (secretName == null) {
            throw new IllegalStateException("SECRET_NAME is not set");
        }

        String secret = this.secretCache.getSecretString(secretName);

        TypeReference<Map<String, String>> typeReference = new TypeReference<>() {};

        Map<String, String> secretMap;

        try {
            secretMap = this.mapper.readValue(secret, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String value = secretMap.get(name);

        if (value == null) {
            throw new IllegalStateException("Secret %s not found".formatted(name));
        }

        return value;
    }
}
