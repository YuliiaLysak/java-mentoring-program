package edu.lysak.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

// Spring Cloud Gateway using Java based config
// (use this class or configurations in application.yaml)
@Configuration
public class SpringCloudConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("oneModule", r -> r
                        .path("/one/**")
                        //Pre and Post Filters provided by Spring Cloud Gateway
                        .filters(f -> f
                                .addRequestHeader("one-request", "one-request-header")
                                .addResponseHeader("one-response", "one-response-header")
                        )
                        .uri("lb://ONE-SERVICE")
                )
                .route("twoModule", r -> r
                        .path("/two/**")
                        //Pre and Post Filters provided by Spring Cloud Gateway
                        .filters(f -> f
                                .addRequestHeader("two-request", "two-request-header")
                                .addResponseHeader("two-response", "two-response-header")
                        )
                        .uri("lb://TWO-SERVICE")
                )
                .build();
    }

    // Use this bean or property in application.yaml
    // default-filters:
    // - name: CustomFilter
    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            System.out.println("First Global filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                System.out.println("Second Global filter");
            }));
        };
    }

}
