package ru.yandex.practicum.my_market_app.model.dto;

public record PageDto(
        int pageNumber,
        int pageSize,
        boolean hasPrevious,
        boolean hasNext
) {
}
