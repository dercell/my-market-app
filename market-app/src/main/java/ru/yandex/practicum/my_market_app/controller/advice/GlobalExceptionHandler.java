package ru.yandex.practicum.my_market_app.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Rendering> handleValidationError(HandlerMethodValidationException ex) {
        log.error("Validation Error: ", ex);

        String validationErrors = ex.getParameterValidationResults()
                .stream().filter(res -> res instanceof ParameterErrors)
                .findFirst()
                .map(res -> res.getResolvableErrors()
                        .stream().map(MessageSourceResolvable::getDefaultMessage)
                        .filter(Objects::nonNull).collect(Collectors.joining("; "))
                ).orElse("");

        return Mono.just(Rendering.view("error")
                .modelAttribute("error", ex.getClass().getSimpleName())
                .modelAttribute("message", validationErrors)
                .modelAttribute("rootCause", ExceptionUtils.getRootCause(ex))
                .modelAttribute("timestamp", LocalDateTime.now())
                .build()
        );
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Rendering> handle500(Exception ex) {
        log.error("Error", ex);
        return Mono.just(Rendering.view("error")
                .modelAttribute("error", ex.getClass().getSimpleName())
                .modelAttribute("message", ex.getMessage())
                .modelAttribute("rootCause", ExceptionUtils.getRootCause(ex))
                .modelAttribute("timestamp", LocalDateTime.now())
                .build()
        );
    }

}
