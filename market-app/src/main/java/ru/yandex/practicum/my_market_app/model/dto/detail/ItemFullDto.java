package ru.yandex.practicum.my_market_app.model.dto.detail;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ItemFullDto {

    private Long id;

    private String title;

    private String description;

    private String imgPath;

    private long price;

    private int count;

    public ItemFullDto(ItemInfoDto item, Integer count) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imgPath = item.getImgPath();
        this.price = item.getPrice();
        this.count = count;
    }

}
