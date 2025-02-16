package net.lbku;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.lbku.model.Champion;
import net.lbku.module.ApplicationModule;
import net.lbku.service.PickService;

public class Application {
    public static void main(String[] args) {
        ApplicationModule module = new ApplicationModule();

        Injector injector = Guice.createInjector(module);

        PickService pickService = injector.getInstance(PickService.class);

        System.out.println(pickService.getPicks(Champion.DRAVEN));
    }
}
