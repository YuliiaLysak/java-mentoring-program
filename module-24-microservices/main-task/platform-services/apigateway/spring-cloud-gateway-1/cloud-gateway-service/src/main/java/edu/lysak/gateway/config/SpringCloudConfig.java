package edu.lysak.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

// Spring Cloud Gateway using Java based config
// (use this class or configurations in application.yaml)
@Slf4j
@Configuration
public class SpringCloudConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("employeeModule", r -> r
                        .path("/employee/**")
                        //Pre and Post Filters provided by Spring Cloud Gateway
                        .filters(f -> f
                                .addRequestHeader("first-request", "first-request-header")
                                .addResponseHeader("first-response", "first-response-header")
                        )
                        .uri("lb://FIRST-SERVICE")
                )
                .route("consumerModule", r -> r
                        .path("/consumer/**")
                        //Pre and Post Filters provided by Spring Cloud Gateway
                        .filters(f -> f
                                .addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header")
                        )
                        .uri("lb://SECOND-SERVICE")
                )
                .build();
    }

    // Use this bean or property in application.yaml
    // default-filters:
    // - name: CustomFilter
    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            log.info("First Global filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Second Global filter");
            }));
        };
    }

}
