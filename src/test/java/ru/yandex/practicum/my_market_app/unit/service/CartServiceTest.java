package ru.yandex.practicum.my_market_app.unit.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("service")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderService orderService;

    private static Stream<Arguments> carts() {
        return Stream.of(Arguments.of(getGoodCart(), 2, 26L), Arguments.of(Flux.empty(), 0, 0L));
    }

    private static Stream<Arguments> changeAmountArgs() {
        return Stream.of(Arguments.of(getGoodCart(), Mono.just(CartItem.builder().id(1L).itemId(1L).count(3).build()), "PLUS", 1, 0), Arguments.of(Flux.fromIterable(List.of(CartItem.builder().id(1L).itemId(1L).count(1).build())), Mono.empty(), "PLUS", 1, 0), Arguments.of(Flux.fromIterable(List.of(CartItem.builder().id(1L).itemId(1L).count(2).build())), Mono.just(CartItem.builder().id(1L).itemId(1L).count(3).build()), "MINUS", 1, 0), Arguments.of(Flux.empty(), Mono.just(CartItem.builder().id(1L).itemId(1L).count(1).build()), "MINUS", 0, 1), Arguments.of(Flux.empty(), Mono.just(CartItem.builder().id(1L).itemId(1L).count(3).build()), "DELETE", 0, 1), Arguments.of(Flux.empty(), Mono.empty(), "MINUS", 0, 0)

        );
    }

    @ParameterizedTest
    @MethodSource("carts")
    void getCart(Flux<CartItem> cart, Integer cartSize, Long totalSum) {

        when(cartRepository.findAll()).thenReturn(cart);

        CartPageDto cartPageDto = cartService.getCart().block();

        assertEquals(cartSize, cartPageDto.itemsList().size());
        assertEquals(totalSum, cartPageDto.totalSum());

    }

    @ParameterizedTest
    @MethodSource("changeAmountArgs")
    void changeItemAmount(Flux<CartItem> cart, Mono<CartItem> cartItem, String action, int saveCallCnt, int deleteCallCnt) {

        when(cartRepository.findAll()).thenReturn(cart);
        when(cartRepository.getCartItemByItemId(1L)).thenReturn(cartItem);

        cartService.changeItemAmount(1L, action);

        verify(cartRepository, times(saveCallCnt)).save(any(CartItem.class));
        verify(cartRepository, times(deleteCallCnt)).delete(any(CartItem.class));
    }

    @Test
    void buy() {
        when(cartRepository.findAll()).thenReturn(getGoodCart());
        when(orderService.buy()).thenReturn(Mono.just(1L));

        Long newOrderId = cartService.buy().block();

        assertEquals(1L, newOrderId);
        verify(cartRepository).deleteAll();

    }

    private static Flux<CartItem> getGoodCart() {
        return Flux.fromIterable(
                List.of(
                        CartItem.builder().id(1L).itemId(1L).count(2).build(),
                        CartItem.builder().id(2L).itemId(2L).count(5).build()
                )
        );
    }

}
