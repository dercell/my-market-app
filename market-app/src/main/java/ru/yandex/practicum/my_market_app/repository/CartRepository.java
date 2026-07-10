package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.util.Collection;


@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {

    Mono<CartItem> getCartItemByItemIdAndUserId(Long itemId, Long userId);

    Flux<CartItem> findAllByItemIdInAndUserId(Collection<Long> itemIds, Long userId);

    Flux<CartItem> findAllByUserId(Long userId);
}
