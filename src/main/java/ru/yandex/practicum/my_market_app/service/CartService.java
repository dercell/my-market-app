package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.repository.ItemRepository;
import ru.yandex.practicum.my_market_app.util.mappers.ItemMapper;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    public Mono<CartPageDto> getCart() {

        return cartRepository.findAll()
                .map(cartItem -> ItemMapper.toDto(cartItem.getItem(), cartItem.getCount()))
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

        Mono<CartItem> cartItemMono = cartRepository.getCartItemByItem_Id(itemId);

        if ("PLUS".equalsIgnoreCase(action)) {
            changeAmountMono = cartItemMono
                    .doOnNext(CartItem::addOne)
                    .flatMap(cartRepository::save)
                    .then()
                    .switchIfEmpty(
                            itemRepository
                                    .findById(itemId)
                                    .map(item -> CartItem.builder().item(item).count(1).build())
                                    .flatMap(cartRepository::save).then()
                    );
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
        Flux<CartItem> cartItems = cartRepository.findAll();
        Mono<Long> newOrderId = orderService.buy(cartItems);

        cartRepository.deleteAll();
        return newOrderId;
    }
}
