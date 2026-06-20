package ru.yandex.practicum.my_market_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;


import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {

        return httpSecurity
                .csrf(withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/items/**", "/images/*").permitAll()
                        .anyExchange().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .oauth2Login(withDefaults())
                .build();

    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        var redirect = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);

        redirect.setPostLogoutRedirectUri("/items");

        return redirect;
    }

}
