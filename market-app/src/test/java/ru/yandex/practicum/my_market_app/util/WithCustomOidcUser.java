package ru.yandex.practicum.my_market_app.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomOidcUserSecurityContextFactory.class)
public @interface WithCustomOidcUser {
    String username() default "username";
    long userId() default 1L;
    String email() default "test@example.com";

}
