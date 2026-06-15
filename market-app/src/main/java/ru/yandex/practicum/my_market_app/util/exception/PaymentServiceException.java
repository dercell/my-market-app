package ru.yandex.practicum.my_market_app.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentServiceException extends RuntimeException {
    private String message;
}
