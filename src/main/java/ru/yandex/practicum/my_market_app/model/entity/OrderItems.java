package ru.yandex.practicum.my_market_app.model.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItems {

    @Id
    @Column
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column
    private Integer count;

}
