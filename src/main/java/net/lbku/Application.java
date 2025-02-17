package net.lbku;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.lbku.model.Champion;
import net.lbku.model.Game;
import net.lbku.module.ApplicationModule;
import net.lbku.service.GameService;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        ApplicationModule module = new ApplicationModule();

        Injector injector = Guice.createInjector(module);

        GameService gameService = injector.getInstance(GameService.class);

        List<Game> games = gameService.getGames(Champion.DRAVEN);

        System.out.printf("%d games found.\n", games.size());

        games.forEach(System.out::println);
    }
}
