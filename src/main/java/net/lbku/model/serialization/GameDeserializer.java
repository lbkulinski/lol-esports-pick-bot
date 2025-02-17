package net.lbku.model.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.lbku.model.Game;

import java.io.IOException;

public final class GameDeserializer extends StdDeserializer<Game> {
    public GameDeserializer() {
        this(null);
    }

    public GameDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Game deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        ObjectCodec codec = parser.getCodec();

        JsonNode node = codec.readTree(parser);

        JsonNode title = node.get("title");

        return codec.treeToValue(title, Game.class);
    }
}
