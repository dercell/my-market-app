package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.entity.OrderItems;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItems, Long> {
}
