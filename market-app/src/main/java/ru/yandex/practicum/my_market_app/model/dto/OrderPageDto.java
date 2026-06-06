package ru.yandex.practicum.my_market_app.model.dto;

import java.util.List;

public record OrderPageDto(Long id, List<ItemDto> items, Long totalSum) {
}
