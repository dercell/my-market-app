package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.repository.CartRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final PaymentService paymentService;
    private final ItemCacheService itemCacheService;

    public Mono<CartPageDto> getCart(Long userId) {

        return getCartItems(userId)
                .flatMap(itemDtoList -> {

                    long totalSum = itemDtoList.stream()
                            .mapToLong(itemDto -> itemDto.getCount() * itemDto.getPrice()).sum();

                    return paymentService.checkBalance(totalSum)
                            .map(pa -> new CartPageDto(itemDtoList, totalSum, pa));
                });
    }

    public Mono<List<ItemFullDto>> getCartItems(Long userId) {
        return cartRepository.findAllByUserId(userId)
                .collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) {
                        return Mono.just(List.of());
                    }
                    List<Long> itemIds = cartItems.stream().map(CartItem::getItemId).toList();
                    return itemCacheService.collectAllItems(itemIds, cartItems);
                });
    }

    public Flux<CartItem> getCartItemsByIdList(List<Long> itemIdList, Long userId) {
        return cartRepository.findAllByItemIdInAndUserId(itemIdList, userId);
    }

    public Mono<CartItem> getCartItemByItemId(Long itemId) {
        return cartRepository.getCartItemByItemId(itemId);
    }

    @Transactional
    public Mono<CartPageDto> changeItemAmount(Long itemId, String action, Long userId) {
        Mono<Void> changeAmountMono;

        Mono<CartItem> cartItemMono = cartRepository.getCartItemByItemId(itemId);
        log.info("itemId: {}, action: {}", itemId, action);
        if ("PLUS".equalsIgnoreCase(action)) {
            changeAmountMono = cartItemMono
                    .flatMap(cartItem -> {
                        log.info("addOne");
                        cartItem.addOne();
                        return cartRepository.save(cartItem);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        log.info("empty");
                        return cartRepository.save(CartItem.builder().itemId(itemId).count(1).userId(userId).build());
                    }))
                    .then();
        } else {
            changeAmountMono = cartItemMono
                    .flatMap(cartItem -> {
                        if ("MINUS".equalsIgnoreCase(action) && cartItem.getCount() > 1) {
                            cartItem.delOne();
                            return cartRepository.save(cartItem).then();
                        } else {
                            return cartRepository.delete(cartItem);
                        }
                    })
                    .switchIfEmpty(Mono.empty());
        }

        return changeAmountMono.then(this.getCart(userId));

    }

    @Transactional
    public Mono<Void> clearCart() {
        return cartRepository.deleteAll();
    }
}
