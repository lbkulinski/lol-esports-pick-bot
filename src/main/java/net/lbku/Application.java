package net.lbku;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.lbku.model.Champion;
import net.lbku.module.ApplicationModule;
import net.lbku.service.GameService;

public class Application {
    public static void main(String[] args) {
        ApplicationModule module = new ApplicationModule();

        Injector injector = Guice.createInjector(module);

        GameService gameService = injector.getInstance(GameService.class);

        gameService.getGames(Champion.DRAVEN)
                   .forEach(System.out::println);
    }
}
