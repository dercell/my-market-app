package ru.yandex.practicum.my_market_app.unit.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.ItemCacheService;
import ru.yandex.practicum.my_market_app.service.PaymentService;

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
    private PaymentService paymentService;

    @Mock
    private ItemCacheService itemCacheService;

    @Test
    void getCartItemByItemId() {
        CartItem expectedItem = new CartItem(1L, 1L, 2);
        when(cartRepository.getCartItemByItemId(1L)).thenReturn(Mono.just(expectedItem));
        CartItem result = cartService.getCartItemByItemId(1L).block();

        assertEquals(expectedItem.getItemId(), result.getItemId());
        assertEquals(expectedItem.getCount(), result.getCount());
    }

    @Test
    void getCartItemsByIdList() {
        List<Long> itemIds = List.of(1L, 2L);
        when(cartRepository.findAllByItemIdIn(itemIds)).thenReturn(Flux.fromIterable(prepareCartItems()));

        List<CartItem> result = cartService.getCartItemsByIdList(itemIds).collectList().block();

        assertEquals(prepareCartItems().size(), result.size());
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void getCartItems(boolean isEmptyCart) {
        List<CartItem> cartItems;
        List<ItemFullDto> items;
        List<Long> itemIds;
        if (isEmptyCart) {
            cartItems = List.of();
            items = List.of();
            itemIds = List.of();
        } else {
            cartItems = prepareCartItems();
            items = getItemFullDtos();
            itemIds = cartItems.stream().map(CartItem::getItemId).toList();
        }
        when(cartRepository.findAll()).thenReturn(Flux.fromIterable(cartItems));
        if (!isEmptyCart) {
            when(itemCacheService.collectAllItems(itemIds, cartItems)).thenReturn(Mono.just(items));
        }

        List<ItemFullDto> resultItems = cartService.getCartItems().block();

        assertEquals(cartItems.size(), resultItems.size());

    }


    @ParameterizedTest
    @MethodSource("carts")
    void getCart(List<CartItem> cart, List<ItemFullDto> fullDtoList, Integer cartSize, long totalSum) {
        when(cartRepository.findAll()).thenReturn(Flux.fromIterable(cart));
        if (!cart.isEmpty()) {
            when(itemCacheService.collectAllItems(List.of(1L, 2L), cart)).thenReturn(Mono.just(fullDtoList));
        }
        when(paymentService.checkBalance(totalSum)).thenReturn(Mono.just(getGoodPA()));

        CartPageDto cartPageDto = cartService.getCart().block();

        assertEquals(cartSize, cartPageDto.itemsList().size());
        assertEquals(totalSum, cartPageDto.totalSum());

    }

    @ParameterizedTest
    @MethodSource("changeAmountCases")
    void changeItemAmount(String caseName, String action, int saveCallCnt, int deleteCallCnt) {


        CartItem changingItem = CartItem.builder().id(1L).itemId(1L).count(0).build();
        List<CartItem> cart = null;

        switch (caseName) {
            case "ORDINARY", "REMOVE_ONE" -> {
                cart = prepareCartItems();
                changingItem.setCount(2);
                List<Long> itemIds = cart.stream().map(CartItem::getItemId).toList();

                when(itemCacheService.collectAllItems(itemIds, cart)).thenReturn(Mono.just(getItemFullDtos()));
                when(cartRepository.save(any(CartItem.class)))
                        .thenReturn(Mono.just(changingItem));
            }
            case "NEW_ITEM" -> {
                cart = List.of(changingItem);
                List<ItemFullDto> fullItems = List.of(new ItemFullDto(1L, "item1", "", "", 10, 1));

                when(itemCacheService.collectAllItems(List.of(1L), cart)).thenReturn(Mono.just(fullItems));
                when(cartRepository.save(any(CartItem.class)))
                        .thenReturn(Mono.just(changingItem));
            }
            case "REMOVE_LAST" -> {
                changingItem.setCount(1);
                cart = List.of();

                when(cartRepository.delete(any(CartItem.class))).thenReturn(Mono.empty());
            }
            case "CLEAR" -> {
                cart = List.of();
                when(cartRepository.delete(any(CartItem.class))).thenReturn(Mono.empty());
            }
            case "REMOVE_EMPTY" -> {
                cart = List.of();
                changingItem = null;
            }

        }
        Mono<CartItem> cartItemMono = changingItem == null ? Mono.empty() : Mono.just(changingItem);

        when(cartRepository.getCartItemByItemId(1L)).thenReturn(cartItemMono);
        when(cartRepository.findAll()).thenReturn(Flux.fromIterable(cart));
        when(paymentService.checkBalance(anyLong())).thenReturn(Mono.just(getGoodPA()));


        cartService.changeItemAmount(1L, action).block();

        verify(cartRepository, times(saveCallCnt)).save(any(CartItem.class));
        verify(cartRepository, times(deleteCallCnt)).delete(any(CartItem.class));
    }

    @Test
    void clearCart() {
        when(cartRepository.deleteAll()).thenReturn(Mono.empty());
        cartService.clearCart().block();

        verify(cartRepository).deleteAll();

    }

    private static Stream<Arguments> changeAmountCases() {
        return Stream.of(
                Arguments.of("ORDINARY", "PLUS", 1, 0),
                Arguments.of("NEW_ITEM", "PLUS", 1, 0),
                Arguments.of("REMOVE_ONE", "MINUS", 1, 0),
                Arguments.of("REMOVE_LAST", "MINUS", 0, 1),
                Arguments.of("CLEAR", "DELETE", 0, 1),
                Arguments.of("REMOVE_EMPTY", "MINUS", 0, 0)
        );
    }

    private static Stream<Arguments> carts() {
        return Stream.of(
                Arguments.of(prepareCartItems(), getItemFullDtos(), 2, 23L),
                Arguments.of(List.of(), List.of(), 0, 0L)
        );
    }

    private static PaymentAvailability getGoodPA() {
        return new PaymentAvailability(true, "Всё в порядке");
    }


    private static List<CartItem> prepareCartItems() {
        return List.of(
                new CartItem(1L, 1L, 2),
                new CartItem(2L, 2L, 1)
        );
    }

    private static List<ItemFullDto> getItemFullDtos() {
        return List.of(
                new ItemFullDto(1L, "item1", "", "", 10, 2),
                new ItemFullDto(2L, "item2", "", "", 3, 1)
        );
    }

}
