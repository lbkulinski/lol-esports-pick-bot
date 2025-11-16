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
    public InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(
        JsonParser jsonParser,
        DeserializationContext deserializationContext
    ) throws IOException {
        String string = jsonParser.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(string, formatter)
                            .atOffset(ZoneOffset.UTC)
                            .toInstant();
    }


}
