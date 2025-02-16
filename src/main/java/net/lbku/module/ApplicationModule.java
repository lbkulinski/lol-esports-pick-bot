package net.lbku.module;

import com.google.inject.AbstractModule;
import net.lbku.provider.WikiProvider;
import org.wikipedia.Wiki;

public final class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(Wiki.class)
            .toProvider(WikiProvider.class);
    }
}
