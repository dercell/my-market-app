package ru.yandex.practicum.my_market_app.model.dto.detail;

import java.util.List;

public record OrderDetailDto(Long id, List<OrderItemDto> items, Long totalSum) {
}
