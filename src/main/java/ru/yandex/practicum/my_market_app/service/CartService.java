package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.dao.ItemDao;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemDao itemDao;
    private final OrderService orderService;

    public Mono<CartPageDto> getCart() {

        return itemDao.getItemsInCart()
                .collectList()
                .flatMap(itemDtoList -> {
                    long totalSum = itemDtoList.stream()
                            .mapToLong(itemDto -> itemDto.count() * itemDto.price()).sum();
                    return Mono.just(new CartPageDto(itemDtoList, totalSum));
                });
    }

    @Transactional
    public Mono<CartPageDto> changeItemAmount(Long itemId, String action) {
        Mono<Void> changeAmountMono;

        Mono<CartItem> cartItemMono = cartRepository.getCartItemByItemId(itemId);
        log.info("itemId: {}, action: {}", itemId, action);
        if ("PLUS".equalsIgnoreCase(action)) {
            changeAmountMono = cartItemMono
                    .doOnNext(CartItem::addOne)
                    .flatMap(cartRepository::save)
                    .switchIfEmpty(cartRepository.save(CartItem.builder().itemId(itemId).count(1).build()))
                    .then();
        } else {
            changeAmountMono = cartItemMono
                    .flatMap(cartItem -> {
                        if (cartItem.getCount() > 1) {
                            cartItem.delOne();
                            return cartRepository.save(cartItem).then();
                        } else {
                            return cartRepository.delete(cartItem);
                        }
                    })
                    .switchIfEmpty(Mono.empty());
        }

        return changeAmountMono.then(this.getCart());

    }

    @Transactional
    public Mono<Long> buy() {
        return orderService.buy()
                .flatMap(newOrderId -> cartRepository
                        .deleteAll().thenReturn(newOrderId));
    }
}
