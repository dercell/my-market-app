package ru.yandex.practicum.my_market_app.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.model.entity.User;

import java.time.Instant;
import java.util.List;

public class WithCustomOidcUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomOidcUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomOidcUser annotation) {

        String username = annotation.username();
        long userId = annotation.userId();
        String email = annotation.email();

        CustomOidcUser authUser = getUser(username, userId, email);

        Authentication authentication = new OAuth2AuthenticationToken(
                authUser,
                List.of(),
                "keycloak"
        );

        return new SecurityContextImpl(authentication);
    }

    public static CustomOidcUser getUser(
            String username,
            long userId,
            String email
    ) {
        OidcIdToken idToken = OidcIdToken.withTokenValue("mock-token")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("sub", "user-" + userId)
                .claim("preferred_username", username)
                .claim("email", email)
                .build();


        OidcUserInfo userInfo = OidcUserInfo.builder()
                .subject("user-" + userId)
                .preferredUsername(username)
                .email(email)
                .build();


        OidcUser oidcUser = new DefaultOidcUser(
                List.of(),
                idToken,
                userInfo,
                "preferred_username"
        );

        User dbUser = User.builder()
                .id(userId)
                .login(username)
                .email(email)
                .build();

        return new CustomOidcUser(oidcUser, dbUser);
    }
}
