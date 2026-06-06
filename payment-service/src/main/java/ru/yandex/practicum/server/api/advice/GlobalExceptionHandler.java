package ru.yandex.practicum.server.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.domain.Error;

import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({WebExchangeBindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<Error>> handle400(WebExchangeBindException webe) {
        log.error("Error", webe);
        List<String> errors = webe.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();


        return Mono.just(ResponseEntity.internalServerError().body(new Error(errors.toString())));
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<Error>> handle500(Exception ex) {
        log.error("Error", ex);
        return Mono.just(ResponseEntity.internalServerError().body(new Error(ex.getMessage())));
    }

}