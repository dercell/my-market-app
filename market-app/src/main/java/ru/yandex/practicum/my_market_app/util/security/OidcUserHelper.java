package ru.yandex.practicum.my_market_app.util.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.model.entity.User;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OidcUserHelper {

    public static Long extractUserIdFromOidcUser(CustomOidcUser oidcUser){
        log.info("Custom user {}", oidcUser);
        return Optional.ofNullable(oidcUser)
                .map(CustomOidcUser::getDbUser)
                .map(User::getId)
                .orElse(-1L);
    }

}
