package ru.yandex.practicum.my_market_app.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class CartItem {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", unique = true)
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
