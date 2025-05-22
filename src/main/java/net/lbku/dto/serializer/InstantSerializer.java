package net.lbku.dto.serializer;

import io.avaje.json.JsonAdapter;
import io.avaje.json.JsonReader;
import io.avaje.json.JsonWriter;
import io.avaje.jsonb.CustomAdapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@CustomAdapter(global = false)
public final class InstantSerializer implements JsonAdapter<Instant> {
    @Override
    public void toJson(JsonWriter jsonWriter, Instant instant) {
        String string = instant.toString();

        jsonWriter.value(string);
    }

    @Override
    public Instant fromJson(JsonReader jsonReader) {
        String string = jsonReader.readString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(string, formatter)
                            .atOffset(ZoneOffset.UTC)
                            .toInstant();
    }
}
