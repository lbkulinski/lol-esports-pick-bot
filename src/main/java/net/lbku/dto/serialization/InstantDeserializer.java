package net.lbku.dto.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class InstantDeserializer extends StdDeserializer<Instant> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(
        JsonParser jsonParser,
        DeserializationContext deserializationContext
    ) throws IOException {
        String string = jsonParser.getText();

        return LocalDateTime.parse(string, FORMATTER)
                            .atOffset(ZoneOffset.UTC)
                            .toInstant();
    }


}
