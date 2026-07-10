package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.model.entity.User;
import ru.yandex.practicum.my_market_app.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public Mono<CustomOidcUser> syncUserInAuth(OidcUser user) {
        return userRepository.getUsersByLogin(user.getName())
                .switchIfEmpty(Mono.just(User.builder()
                        .login(user.getName())
                        .firstName(user.getGivenName())
                        .lastName(user.getFamilyName())
                        .email(user.getEmail()).build()))
                .flatMap(dbUser -> {
                    dbUser.setLastLoginDttm(LocalDateTime.now());
                    return userRepository.save(dbUser);
                })
                .map(dbUser -> new CustomOidcUser(user, dbUser));
    }

}
