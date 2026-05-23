package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;


@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {
    
    Mono<CartItem> getCartItemByItem_Id(Long itemId);

}
