package ru.yandex.practicum.my_market_app.model.dto.detail;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class OrderItemDto {

    private Long id;

    private String title;

    private long price;

    private int count;

}
