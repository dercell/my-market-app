package ru.yandex.practicum.my_market_app.util.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toDto(Item item, int count) {
        return new ItemDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice(),
                count
        );
    }

}