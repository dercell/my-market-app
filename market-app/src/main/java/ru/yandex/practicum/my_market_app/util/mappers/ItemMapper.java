package ru.yandex.practicum.my_market_app.util.mappers;

import io.r2dbc.spi.Readable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderItemDto;

import java.util.function.Function;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Function<? super Readable, ItemFullDto> itemDtoRowMapper() {
        return row ->
                new ItemFullDto(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("description", String.class),
                        row.get("img_path", String.class),
                        row.get("price", Long.class),
                        row.get("count", Integer.class)
                );
    }

    public static Function<? super Readable, OrderItemDto> toOrderItemDto() {
        return row ->
                new OrderItemDto(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("price", Long.class),
                        row.get("count", Integer.class)
                );
    }

    public static Function<? super Readable, ItemInfoDto> toStrippedDto() {
        return row ->
                new ItemInfoDto(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("description", String.class),
                        row.get("img_path", String.class),
                        row.get("price", Long.class)
                );
    }

}