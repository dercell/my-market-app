package ru.yandex.practicum.my_market_app.model.dto.detail;

public record ItemDetailDto(
        Long id,
        String title,
        String description,
        String imgPath,
        long price,
        int count) {
}
