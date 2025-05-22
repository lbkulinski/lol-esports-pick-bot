package net.lbku.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.jsonb.Jsonb;

@Factory
public final class JsonbFactory {
    @Bean
    public Jsonb buildJsonb() {
        return Jsonb.builder()
                    .build();
    }
}
