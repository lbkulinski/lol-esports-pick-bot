package net.lbku.provider;

import com.google.inject.Provider;
import net.lbku.config.BotConfiguration;
import org.aeonbits.owner.ConfigFactory;

public final class BotConfigurationProvider implements Provider<BotConfiguration> {
    @Override
    public BotConfiguration get() {
        return ConfigFactory.create(BotConfiguration.class);
    }
}
