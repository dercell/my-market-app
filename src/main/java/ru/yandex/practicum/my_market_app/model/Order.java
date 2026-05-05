package ru.yandex.practicum.my_market_app.model;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String status;

    @Column(name = "total_sum")
    private Long totalSum;


}
