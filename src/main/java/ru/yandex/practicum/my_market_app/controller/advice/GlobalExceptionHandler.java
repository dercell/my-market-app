package ru.yandex.practicum.my_market_app.controller.advice;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle500(Exception ex, Model model) {
        model.addAttribute("error", ex.getClass().getSimpleName());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("rootCause", ExceptionUtils.getRootCause(ex));
        model.addAttribute("timestamp", LocalDateTime.now());
        return "error";
    }

}
