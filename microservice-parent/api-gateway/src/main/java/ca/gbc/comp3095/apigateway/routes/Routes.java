package ca.gbc.comp3095.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;


@Configuration
@Slf4j
public class Routes {

    @Value("${service.wellness-url}")
    private String wellnessServiceUrl;

    @Value("${service.event-url}")
    private String eventServiceUrl;

    @Value("${service.goal-url}")
    private String goalTrackingServiceUrl;

    @Value("${services.api-gateway-fallback-url}")
    private String apiGatewayFallbackUrl;

    @Bean
    public RouterFunction<ServerResponse> wellnessServiceRoute() {

        log.info("Initializing wellness-service route with URL {}", wellnessServiceUrl);
        return GatewayRouterFunctions.route("wellness_service")
                .route(
                        RequestPredicates.path("/api/wellness-resource/**"),
                        HandlerFunctions.http(wellnessServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("wellnessServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl))
                ).build();

    }


    @Bean
    public RouterFunction<ServerResponse> eventServiceRoute() {

        log.info("Initializing event-service route with URL {}", eventServiceUrl);

        return GatewayRouterFunctions.route("event_service")
                .route(
                        RequestPredicates.path("/api/event/**"),
                        HandlerFunctions.http(eventServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("eventServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl))
                ).build();

    }


    @Bean
    public RouterFunction<ServerResponse> goalTrackingServiceRoute() {

        log.info("Initializing goal-tracking-service route with URL {}", goalTrackingServiceUrl);

        return GatewayRouterFunctions.route("goal_tracking_service")
                .route(
                        RequestPredicates.path("/api/goal/**"),
                        HandlerFunctions.http(goalTrackingServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("goalServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl))
                ).build();

    }

    @Bean
    public RouterFunction<ServerResponse> wellnessServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("wellness_service_swagger")
                .route(
                        RequestPredicates.path("/aggregate/wellness-service/v3/api-docs"),
                        HandlerFunctions.http(wellnessServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("wellnessServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl)))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> eventServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("event_service_swagger")
                .route(
                        RequestPredicates.path("/aggregate/event-service/v3/api-docs"),
                        HandlerFunctions.http(eventServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("eventServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl)))
                .filter(setPath("/api-docs"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> goalServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("goal_service_swagger")
                .route(
                        RequestPredicates.path("/aggregate/goal-service/v3/api-docs"),
                        HandlerFunctions.http(goalTrackingServiceUrl)
                )
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("goalServiceCircuitBreaker",
                        URI.create("forward:" + apiGatewayFallbackUrl)))
                .filter(setPath("/api-docs"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> fallbackRoutes() {
        log.info("Falling back to default routes");
        return route("fallBackRoute")
                .route(RequestPredicates.path(apiGatewayFallbackUrl),
                        request -> {
                            log.warn("Fallback handler invoked for original request: {}",
                                    request.attribute("org.springframework.cloud.gateway.server.mvc.HandlerFunctions.originalRequestUrl"));

                            return ServerResponse
                                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                                    .body("Service Unavailable, please try again later");
                        })
                .build();

    }


}