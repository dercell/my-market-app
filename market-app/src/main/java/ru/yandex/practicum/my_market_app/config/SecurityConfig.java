package ru.yandex.practicum.my_market_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
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

    public void configureCommon(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/items/create").authenticated()
                        .pathMatchers(HttpMethod.POST, "/items/**", "/images/*").authenticated()
                        .pathMatchers("/", "/items/**", "/images/*").permitAll()
                        .anyExchange().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .oauth2Login(login ->
                        login.authenticationSuccessHandler(successHandler)

                )
                .oauth2Client(withDefaults())
                .anonymous(anon -> anon.principal("Гость"));
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {

        configureCommon(httpSecurity);

        return httpSecurity
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                )
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
