package ru.yandex.practicum.my_market_app.model.dto;

import java.util.List;

public record CartPageDto(
        List<ItemDto> itemsList,
        Long totalSum
) {
}
