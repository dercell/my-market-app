package ru.yandex.practicum.my_market_app.model.dto.detail;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemInfoDto {

    private Long id;

    private String title;

    private String description;

    private String imgPath;

    private long price;

}
