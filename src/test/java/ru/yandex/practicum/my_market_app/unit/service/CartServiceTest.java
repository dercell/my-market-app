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
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.repository.ItemRepository;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;
import java.util.Optional;
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
    private ItemRepository itemRepository;

    @Mock
    private OrderService orderService;

    private static Stream<Arguments> carts() {
        return Stream.of(
                Arguments.of(getGoodCart(), 2, 26L),
                Arguments.of(List.of(), 0, 0L)
        );
    }

    private static Stream<Arguments> changeAmountArgs() {
        return Stream.of(
                Arguments.of(getGoodCart(),
                        Optional.of(CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(3).build()), "PLUS", 1, 0),
                Arguments.of(List.of(CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(1).build()),
                        Optional.empty(), "PLUS", 1, 0),
                Arguments.of(List.of(
                                CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(2).build()),
                        Optional.of(CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(3).build()), "MINUS", 1, 0),
                Arguments.of(List.of(),
                        Optional.of(CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(1).build()), "MINUS", 0, 1),
                Arguments.of(List.of(),
                        Optional.of(CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(3).build()), "DELETE", 0, 1),
                Arguments.of(List.of(), Optional.empty(), "MINUS", 0, 0)

        );
    }

    @ParameterizedTest
    @MethodSource("carts")
    void getCart(List<CartItem> cart, Integer cartSize, Long totalSum) {

        when(cartRepository.findAll()).thenReturn(cart);

        CartPageDto cartPageDto = cartService.getCart();

        assertEquals(cartSize, cartPageDto.itemsList().size());
        assertEquals(totalSum, cartPageDto.totalSum());

    }

    @ParameterizedTest
    @MethodSource("changeAmountArgs")
    void changeItemAmount(List<CartItem> cart, Optional<CartItem> cartItem, String action, int saveCallCnt, int deleteCallCnt) {

        when(cartRepository.findAll()).thenReturn(cart);
        when(cartRepository.getCartItemByItem_Id(1L)).thenReturn(cartItem);

        cartService.changeItemAmount(1L, action);

        verify(cartRepository, times(saveCallCnt)).save(any(CartItem.class));
        verify(cartRepository, times(deleteCallCnt)).delete(any(CartItem.class));
    }

    @Test
    void buy() {
        when(cartRepository.findAll()).thenReturn(getGoodCart());
        when(orderService.buy(getGoodCart())).thenReturn(1L);

        Long newOrderId = cartService.buy();

        assertEquals(1L, newOrderId);
        verify(cartRepository).deleteAll();

    }

    private static List<CartItem> getGoodCart() {
        return List.of(
                CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(2).build(),
                CartItem.builder().id(2L).item(Item.builder().id(2L).title("item2").description("").price(4L).imgPath("").build()).count(5).build()
        );
    }

}
