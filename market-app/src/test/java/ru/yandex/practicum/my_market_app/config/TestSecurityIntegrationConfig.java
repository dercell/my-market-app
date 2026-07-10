package ru.yandex.practicum.my_market_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityIntegrationConfig {

    @Autowired
    private SecurityConfig securityConfig;

    @Bean
    @Primary
    public SecurityWebFilterChain testSecurityWebFilterChain(ServerHttpSecurity httpSecurity) {
        securityConfig.configureCommon(httpSecurity);

        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}
