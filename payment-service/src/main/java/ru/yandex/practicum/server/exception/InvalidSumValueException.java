package ru.yandex.practicum.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidSumValueException extends RuntimeException {
    private String message;
}
