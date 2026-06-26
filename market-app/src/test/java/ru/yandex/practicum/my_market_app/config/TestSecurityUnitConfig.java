package ru.yandex.practicum.my_market_app.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityUnitConfig {

    @Bean
    @Primary
    public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/items/create").authenticated()
                        .pathMatchers(HttpMethod.POST, "/items/**", "/images/*").authenticated()
                        .pathMatchers("/", "/items/**", "/images/*").permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }

}
