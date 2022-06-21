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

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class SportRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(SportHandler sportHandler) {

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

    @Bean
    RouterFunction<ServerResponse> restRoutes(SportHandler handler) {
        return RouterFunctions
                .route(GET("/handler/sports")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getAllSports)
                .andRoute(GET("/handler/sports/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getSportById)
                .andRoute(GET("/handler/sports/name/{name}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::getSportByName)
                .andRoute(POST("/handler/sports")
                                .and(accept(MediaType.APPLICATION_JSON))
                                .and(contentType(MediaType.APPLICATION_JSON)), handler::saveSportWithBody)
                .andRoute(PUT("/handler/sports/{id}")
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateSportById)
                .andRoute(DELETE("/handler/sports/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), handler::deleteSportById);
    }

}
