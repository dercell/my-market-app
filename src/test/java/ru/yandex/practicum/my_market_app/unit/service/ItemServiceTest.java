package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("service")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemDao itemDao;

    @Mock
    private CartService cartService;

    @Test
    void getItemsPage() {

        List<ItemDto> cartPageDto = List.of(new ItemDto(1L, "item1", "", "", 1L, 3));

        when(itemDao.getTotalRows(anyString())).thenReturn(Mono.just(1L));
        when(itemDao.getItemPage(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Flux.fromIterable(cartPageDto));


        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 5, "NO").block();

        assertEquals(1, itemPageDto.items().stream().mapToLong(List::size).sum());
        assertEquals(0, itemPageDto.paging().pageNumber());
        assertEquals(5, itemPageDto.paging().pageSize());
        assertEquals("NO", itemPageDto.sort());
    }

    @Test
    void getItem() {
        ItemDto testItemDto = new ItemDto(1L, "item1", "", "", 1L, 3);
        when(itemDao.getItem(1L)).thenReturn(Mono.just(testItemDto));
        ItemDto itemDto = itemService.getItem(1L).block();

        assertEquals(testItemDto.title(), itemDto.title());
    }

    @Test
    void changeAmount() {
        when(cartService.changeItemAmount(1L, "PLUS")).thenReturn(Mono.empty());

        itemService.changeItemAmount(1L, "PLUS").block();

        verify(cartService).changeItemAmount(1L, "PLUS");
    }

}
