package ru.yandex.practicum.my_market_app.util.validation.itemform;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ItemFormValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemFormValid {
    String message() default "Item ID and Action must be specified";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
