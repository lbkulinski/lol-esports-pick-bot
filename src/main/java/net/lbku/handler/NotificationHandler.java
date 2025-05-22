package net.lbku.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.avaje.inject.BeanScope;
import net.lbku.service.PostService;

@SuppressWarnings("unused")
public class NotificationHandler implements RequestHandler<Void, Void> {
    @Override
    public Void handleRequest(Void unused, Context context) {
        try (BeanScope beanScope = BeanScope.builder()
                                            .build()) {
            PostService service = beanScope.get(PostService.class);

            service.postNewGames();
        }

        return null;
    }

    public static void main(String[] args) {
        try (BeanScope beanScope = BeanScope.builder()
                                            .build()) {
            PostService service = beanScope.get(PostService.class);

            service.postNewGames();
        }
    }
}
