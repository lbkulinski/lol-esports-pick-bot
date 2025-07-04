package net.lbku.model;

public enum Champion {
    DRAVEN,
    /*JHIN,
    LUCIAN*/;

    @Override
    public String toString() {
        String name = this.name();

        String firstLetter = name.substring(0, 1)
                                 .toUpperCase();

        String rest = name.substring(1)
                          .toLowerCase();

        return firstLetter + rest;
    }
}
