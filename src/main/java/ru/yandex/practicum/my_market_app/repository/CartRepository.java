package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.util.Optional;


@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {
    
    Optional<CartItem> getCartItemByItem_Id(Long itemId);

}
