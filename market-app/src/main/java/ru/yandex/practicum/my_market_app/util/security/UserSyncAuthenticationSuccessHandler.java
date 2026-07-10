package ru.yandex.practicum.my_market_app.util.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.service.UserService;

@Slf4j
@Component
@AllArgsConstructor
public class UserSyncAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final UserService userService;
    private final RedirectServerAuthenticationSuccessHandler redirect = new RedirectServerAuthenticationSuccessHandler("/");

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.justOrEmpty(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .cast(OidcUser.class)
                .flatMap(userService::syncUserInAuth)
                .doOnNext(user -> log.info("User {} logged in {}", user.getDbUser().getLogin(), user.getDbUser().getLastLoginDttm()))
                .flatMap(enrichedUser -> {
                    var newAuth = new OAuth2AuthenticationToken(enrichedUser, authentication.getAuthorities(), "keycloak");
                    return ReactiveSecurityContextHolder.getContext()
                            .map(context -> {
                                context.setAuthentication(newAuth);
                                return context;
                            })
                            .then(redirect
                                    .onAuthenticationSuccess(webFilterExchange, authentication));
                }).switchIfEmpty(redirect
                        .onAuthenticationSuccess(webFilterExchange, authentication));
    }
}
