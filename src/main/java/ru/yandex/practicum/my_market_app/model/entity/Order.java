package ru.yandex.practicum.my_market_app.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @Column
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_sum")
    private Long totalSum;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;

}
