package net.lbku.dto.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class BooleanDeserializer extends StdDeserializer<Boolean> {
    private static final String TRUE_MAPPING = "Yes";

    public BooleanDeserializer(Class<?> vc) {
        super(vc);
    }

    public BooleanDeserializer() {
        this(null);
    }

    @Override
    public Boolean deserialize(
        JsonParser jsonParser,
        DeserializationContext deserializationContext
    ) throws IOException {
        String string = jsonParser.getText();

        return string.equalsIgnoreCase(TRUE_MAPPING);
    }
}
