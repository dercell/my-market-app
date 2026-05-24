package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("service")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private CartService cartService;

    @Test
    void getItemsPage() {

        CartPageDto cartPageDto = new CartPageDto(
                List.of(new ItemDto(1L, "item1", "", "", 1L, 3)),
                9L
        );

        when(cartService.getCart()).thenReturn(Mono.just(cartPageDto));

        itemService.getItemsPage("", 0, 5, "NO")
                .doOnNext(itemPageDto -> {
                    assertEquals(1, itemPageDto.items().stream().mapToLong(List::size).sum());
                    assertEquals(0, itemPageDto.paging().pageNumber());
                    assertEquals(5, itemPageDto.paging().pageSize());
                    assertEquals("NO", itemPageDto.sort());
                }).block();
    }

    @Test
    void getItem() {
        Item item = Item.builder().id(1L).title("item1").description("").price(1L).imgPath("").build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<ItemDto> itemDto = itemService.getItem(1L);

        assertEquals(item.getTitle(), itemDto.map(ItemDto::title).orElse(null));
    }

    @Test
    void changeAmount() {
        itemService.changeItemAmount(1L, "PLUS");

        verify(cartService).changeItemAmount(1L, "PLUS");
    }

}
