package ru.yandex.practicum.my_market_app.model.dto;

import java.util.List;

public record ItemPageDto(
        List<List<ItemDto>> items,
        String search,
        PageDto paging
) {
}
