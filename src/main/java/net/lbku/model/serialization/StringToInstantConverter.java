package net.lbku.model.serialization;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class StringToInstantConverter extends StdConverter<String, Instant> {
    @Override
    public Instant convert(String string) {
        Objects.requireNonNull(string);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(string, formatter)
                            .atOffset(ZoneOffset.UTC)
                            .toInstant();
    }
}
