package net.lbku.dto.serializer;

import io.avaje.json.JsonAdapter;
import io.avaje.json.JsonReader;
import io.avaje.json.JsonWriter;
import io.avaje.jsonb.CustomAdapter;

@CustomAdapter(global = false)
public final class BooleanSerializer implements JsonAdapter<Boolean> {
    @Override
    public void toJson(JsonWriter jsonWriter, Boolean aBoolean) {
        jsonWriter.value(aBoolean);
    }

    @Override
    public Boolean fromJson(JsonReader jsonReader) {
        String string = jsonReader.readString();

        return string.equalsIgnoreCase("Yes");
    }
}
