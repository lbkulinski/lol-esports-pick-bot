package net.lbku.provider;

import com.google.inject.Provider;
import org.wikipedia.Wiki;

public final class WikiProvider implements Provider<Wiki> {
    @Override
    public Wiki get() {
        return Wiki.newSession("lol.fandom.com", "/", "https://");
    }
}
