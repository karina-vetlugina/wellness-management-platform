package ca.gbc.comp3095.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;


@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Value("${service.api-gateway-authority-prefix}")
    private String authorityPrefix;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${services.api-gateway-fallback-url}")
    private String apiGatewayFallbackUrl;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Security Filter Chain");

        return httpSecurity.csrf(AbstractHttpConfigurer::disable) // Disable CSRF (temporarily)
                // Authorize all HTTP requests, requiring authentification
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET,
                            "/api/wellness-resource/**",
                            "/api/event/**",
                            "/api/goal/**"
                    ).hasAnyRole("student", "staff", "admin");

                    authorize.requestMatchers(HttpMethod.POST,
                            "/api/goal/**",
                            "/api/event/*/register/**"
                    ).hasAnyRole("student", "admin");
                    authorize.requestMatchers(HttpMethod.PATCH,
                            "/api/goal/*/complete/**"
                    ).hasAnyRole("student", "admin");

                    authorize.requestMatchers(HttpMethod.POST,
                            "/api/wellness-resource/**",
                            "/api/event/**"
                    ).hasAnyRole("staff", "admin");
                    authorize.requestMatchers(HttpMethod.PUT,
                            "/api/wellness-resource/**",
                            "/api/event/**"
                    ).hasAnyRole("staff", "admin");
                    authorize.requestMatchers(HttpMethod.DELETE,
                            "/api/wellness-resource/**",
                            "/api/event/**"
                    ).hasAnyRole("staff", "admin");
                    authorize.requestMatchers(apiGatewayFallbackUrl).hasAnyRole("student", "staff", "admin");

                    authorize.requestMatchers("/**").hasRole("admin");

                    authorize.anyRequest().denyAll();
                })
                // Set up OAuth2 server to use JWT token for authentification
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();

    }


    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = issuer + "/protocol/openid-connect/certs";
        log.info("🔧 Configuring JWT Decoder via JWK Set URI: {}", jwkSetUri);

        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.get("roles") == null) {
                return java.util.List.of();
            }

            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(role -> authorityPrefix + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(java.util.stream.Collectors.toList());
        });

        return converter;
    }
}

