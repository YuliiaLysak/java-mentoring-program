package edu.lysak.reactPractice.hello.config;

import edu.lysak.reactPractice.hello.handler.GreetingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class GreetingRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {

        RequestPredicate helloSimpleRoute = RequestPredicates
                .GET("/hello-simple")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        RequestPredicate helloRoute = RequestPredicates
                .GET("/hello")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions
                .route(helloSimpleRoute, greetingHandler::helloSimple)
                .andRoute(helloRoute, greetingHandler::hello);
    }
}
