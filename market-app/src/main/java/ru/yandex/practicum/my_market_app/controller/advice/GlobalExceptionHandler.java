package ru.yandex.practicum.my_market_app.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

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
