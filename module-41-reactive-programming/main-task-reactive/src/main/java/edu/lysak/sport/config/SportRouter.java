package edu.lysak.sport.config;

import edu.lysak.sport.handler.SportHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SportRouter {

    @Bean
    public RouterFunction<ServerResponse> route(SportHandler sportHandler) {

        RequestPredicate getSportRoute = RequestPredicates
                .GET("/api/v1/sport")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        RequestPredicate saveSportRouteV1 = RequestPredicates
                .POST("/api/v1/sport/{sportname}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        RequestPredicate saveSportRouteV2 = RequestPredicates
                .POST("/api/v2/sport/{sportname}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        RequestPredicate saveSportRouteV3 = RequestPredicates
                .POST("/api/v3/sport/{sportname}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions
                .route(getSportRoute, sportHandler::getSport)
                .andRoute(saveSportRouteV1, sportHandler::saveSportV1)
                .andRoute(saveSportRouteV2, sportHandler::handleAddingSportEntity)
                .andRoute(saveSportRouteV3, sportHandler::saveSportV3);
    }

}
