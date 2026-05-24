package ru.yandex.practicum.my_market_app.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @Column
    @EqualsAndHashCode.Include
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Long price;

    @Column
    private String imgPath;

}
