package ru.yandex.practicum.my_market_app.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class CartItem {

    @Id
    @Column
    @EqualsAndHashCode.Include
    private Long id;

    @Column("item_id")
    private Long itemId;

    @Column
    private Integer count;

    @Column("user_id")
    private Long userId;

    public void addOne() {
        this.count++;
    }

    public void delOne() {
        if (count > 1) {
            this.count--;
        }
    }

}
