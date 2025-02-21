package net.lbku;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.lbku.module.ApplicationModule;
import net.lbku.service.PostService;

public class Application {
    public static void main(String[] args) {
        ApplicationModule module = new ApplicationModule();

        Injector injector = Guice.createInjector(module);

        PostService service = injector.getInstance(PostService.class);

        service.postNewGames();
    }
}
