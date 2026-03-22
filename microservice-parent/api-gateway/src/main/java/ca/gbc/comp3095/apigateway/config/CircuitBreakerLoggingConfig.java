    package ca.gbc.comp3095.apigateway.config;


    import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
    import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
    import jakarta.annotation.PostConstruct;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.event.ContextRefreshedEvent;
    import org.springframework.context.event.EventListener;
    import org.springframework.stereotype.Component;

    @Component
    @Slf4j
    public class CircuitBreakerLoggingConfig {

        private final CircuitBreakerRegistry registry;

        public CircuitBreakerLoggingConfig(CircuitBreakerRegistry registry) {
            this.registry = registry;
        }

        @EventListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            registry.getAllCircuitBreakers().forEach(cb ->
                    cb.getEventPublisher().onStateTransition(e ->
                            log.warn("CircuitBreaker '{}' STATE CHANGE: {} → {}",
                                    e.getCircuitBreakerName(),
                                    e.getStateTransition().getFromState(),
                                    e.getStateTransition().getToState())
                    )
            );
        }
    }
