package ru.yandex.practicum.my_market_app.model.dto;

public record ItemDto(
        Long id,
        String title,
        String description,
        String imgPath,
        long price,
        int count) {
}
