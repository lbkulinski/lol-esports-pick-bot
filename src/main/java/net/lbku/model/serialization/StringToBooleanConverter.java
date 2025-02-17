package net.lbku.model.serialization;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Objects;

public final class StringToBooleanConverter extends StdConverter<String, Boolean> {
    @Override
    public Boolean convert(String string) {
        Objects.requireNonNull(string);

        String uppercaseString = string.toUpperCase();

        return uppercaseString.equals("YES");
    }
}
