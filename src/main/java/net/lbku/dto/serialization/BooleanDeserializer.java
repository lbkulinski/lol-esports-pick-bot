package net.lbku.dto.serialization;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public final class BooleanDeserializer extends StdDeserializer<Boolean> {
    private static final String TRUE_MAPPING = "Yes";

    public BooleanDeserializer() {
        super(Boolean.class);
    }

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext context) {
        String string = jsonParser.getString();

        return string.equalsIgnoreCase(TRUE_MAPPING);
    }
}
