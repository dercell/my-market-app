package ru.yandex.practicum.my_market_app.util.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;


@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static RowMapper<ItemDto> itemDtoRowMapper() {
        return (rs, rowNum) -> new ItemDto(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("img_path"),
                rs.getLong("price"),
                rs.getInt("count")
        );
    }

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