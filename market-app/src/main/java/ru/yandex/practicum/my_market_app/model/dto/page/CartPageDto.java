package ru.yandex.practicum.my_market_app.model.dto.page;

import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;

import java.util.List;

public record CartPageDto(
        List<ItemFullDto> itemsList,
        Long totalSum,
        PaymentAvailability paymentAvailability
) {
}
