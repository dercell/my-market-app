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
import ru.yandex.practicum.my_market_app.util.security.UserSyncAuthenticationSuccessHandler;


import java.net.URI;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserSyncAuthenticationSuccessHandler successHandler;

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
                .oauth2Login(login ->
                        login.authenticationSuccessHandler(successHandler)

                )
                //.oauth2Login(withDefaults())
                .oauth2Client(withDefaults())
                .anonymous(anon -> anon.principal("Гость"))
                .build();

    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        var handler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);

        handler.setPostLogoutRedirectUri("{baseUrl}/items");
        handler.setLogoutSuccessUrl(URI.create("/items"));

        return handler;
    }

}
