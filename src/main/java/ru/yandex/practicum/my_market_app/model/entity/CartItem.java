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
@Table(name = "cart")
public class CartItem {

    @Id
    @Column
    @EqualsAndHashCode.Include
    private Long id;


    private Item item;

    @Column
    private Integer count;

    public void addOne() {
        this.count++;
    }

    public void delOne() {
        if (count > 1) {
            this.count--;
        }

    }

}
