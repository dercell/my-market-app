package ru.yandex.practicum.my_market_app.model.dto.page;

import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;

import java.util.List;

public record ItemPageDto(
        List<List<ItemFullDto>> items,
        String search,
        String sort,
        PageDto paging
) {
}
