package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.service.ItemCacheService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("service")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ItemCacheServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, ItemInfoDto> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, ItemInfoDto> valueOperations;

    @Mock
    private ItemDao itemDao;

    @InjectMocks
    private ItemCacheService itemCacheService;

    @Test
    void getCachedItems() {
        List<Long> idList = List.of(1L, 2L, 3L);
        List<String> expectedKeys = List.of("itemInfo:1", "itemInfo:2", "itemInfo:3");

        ItemInfoDto item1 = ItemInfoDto.builder().id(1L).build();
        ItemInfoDto item2 = ItemInfoDto.builder().id(2L).build();

        List<ItemInfoDto> redisValues = List.of(item1, item2, null);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(expectedKeys)).thenReturn(Mono.just(redisValues));

        List<ItemInfoDto> cachedItemsRes = itemCacheService.getCachedItems(idList).block();

        assertEquals(2, cachedItemsRes.size());
        assertEquals(item1.getId(), cachedItemsRes.stream().filter(item -> item.getId() == 1).mapToLong(ItemInfoDto::getId).findFirst().orElse(0L));

    }

    @Test
    void cacheItem() {
        ItemInfoDto item1 = ItemInfoDto.builder().id(1L).build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set("itemInfo:1", item1, Duration.of(1, ChronoUnit.MINUTES)))
                .thenReturn(Mono.just(Boolean.TRUE));

        ItemInfoDto cachedItem = itemCacheService.cacheItem(item1).block();

        assertEquals(item1.getId(), cachedItem.getId());
    }

    @Test
    void getCachedItem() {
        ItemInfoDto item1 = ItemInfoDto.builder().id(1L).build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndExpire("itemInfo:1", Duration.of(1, ChronoUnit.MINUTES)))
                .thenReturn(Mono.just(item1));

        ItemInfoDto cachedItem = itemCacheService.getCachedItem(1L).block();

        assertEquals(item1.getId(), cachedItem.getId());
    }

    @Test
    void collectAllItems() {

        List<Long> idList = List.of(1L, 2L, 3L);
        List<String> expectedKeys = List.of("itemInfo:1", "itemInfo:2", "itemInfo:3");

        ItemInfoDto item1 = ItemInfoDto.builder().id(1L).build();
        ItemInfoDto item2 = ItemInfoDto.builder().id(2L).build();
        ItemInfoDto item3 = ItemInfoDto.builder().id(3L).build();
        List<ItemInfoDto> redisValues = Arrays.asList(item1, item2, null);

        List<CartItem> cartItems = List.of(
                new CartItem(1L, 1L, 2, 1L),
                new CartItem(2L, 2L, 2, 1L),
                new CartItem(3L, 3L, 2, 1L)
        );

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(expectedKeys)).thenReturn(Mono.just(redisValues));
        when(itemDao.getItemsByIdList(List.of(3L))).thenReturn(Flux.fromIterable(List.of(item3)));
        when(valueOperations.set("itemInfo:3", item3, Duration.of(1, ChronoUnit.MINUTES)))
                .thenReturn(Mono.just(Boolean.TRUE));

        List<ItemFullDto> res = itemCacheService.collectAllItems(idList, cartItems).block();

        assertEquals(cartItems.size(), res.size());

    }

}
