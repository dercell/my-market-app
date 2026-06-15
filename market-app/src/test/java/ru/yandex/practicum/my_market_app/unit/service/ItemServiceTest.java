package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.ItemCacheService;
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

    @Mock
    private ItemCacheService itemCacheService;

    @Test
    void getItemsPage() {

        List<ItemFullDto> cartPageDto = List.of(new ItemFullDto(1L, "item1", "", "", 1L, 3));
        CartItem ca = new CartItem(1L, 1L, 0);
        when(itemDao.getTotalRows(anyString())).thenReturn(Mono.just(1L));
        when(itemDao.getItemIdsPage(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Flux.just(1L));
        when(cartService.getCartItemsByIdList(List.of(1L))).thenReturn(Flux.just(ca));

        when(itemCacheService.collectAllItems(List.of(1L), List.of(ca))).thenReturn(Mono.just(cartPageDto));

        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 5, "NO").block();

        assertEquals(1, itemPageDto.items().stream().mapToLong(List::size).sum());
        assertEquals(0, itemPageDto.paging().pageNumber());
        assertEquals(5, itemPageDto.paging().pageSize());
        assertEquals("NO", itemPageDto.sort());
    }


    @ParameterizedTest
    @CsvSource({"true", "false"})
    void getItemFromDb(boolean isCached) {
        ItemFullDto expected = new ItemFullDto(1L, "item1", "", "", 1L, 3);
        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "item1", "", "", 1L);
        CartItem ca = new CartItem(1L, 1L, 3);
        Mono<ItemInfoDto> cachedItemsMono = null;
        if (isCached) {
            cachedItemsMono = Mono.just(itemInfoDto);
        } else {
            cachedItemsMono = Mono.empty();
            when(itemCacheService.cacheItem(itemInfoDto)).thenReturn(Mono.empty());
        }

        when(itemCacheService.getCachedItem(1L)).thenReturn(cachedItemsMono);

        when(cartService.getCartItemByItemId(1L)).thenReturn(Mono.just(ca));
        when(itemDao.getItem(1L)).thenReturn(Mono.just(itemInfoDto));

        ItemFullDto itemFullDto = itemService.getItem(1L).block();

        assertEquals(expected.getTitle(), itemFullDto.getTitle());
        assertEquals(expected.getCount(), itemFullDto.getCount());
    }


    @Test
    void changeAmount() {
        when(cartService.changeItemAmount(1L, "PLUS")).thenReturn(Mono.empty());

        itemService.changeItemAmount(1L, "PLUS").block();

        verify(cartService).changeItemAmount(1L, "PLUS");
    }

}
