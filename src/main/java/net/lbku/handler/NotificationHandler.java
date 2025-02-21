package net.lbku.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.lbku.module.ApplicationModule;
import net.lbku.service.PostService;

public final class NotificationHandler implements RequestHandler<Void, Void> {
    @Override
    public Void handleRequest(Void unused, Context context) {
        ApplicationModule module = new ApplicationModule();

        Injector injector = Guice.createInjector(module);

        PostService service = injector.getInstance(PostService.class);

        service.postNewGames();

        return null;
    }
}
