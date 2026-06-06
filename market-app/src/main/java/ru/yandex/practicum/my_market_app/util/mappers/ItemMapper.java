package ru.yandex.practicum.my_market_app.util.mappers;

import io.r2dbc.spi.Readable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;

import java.util.function.Function;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Function<? super Readable, ItemDto> itemDtoRowMapper() {
        return row ->
                new ItemDto(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("description", String.class),
                        row.get("img_path", String.class),
                        row.get("price", Long.class),
                        row.get("count", Integer.class)
                );
    }

}