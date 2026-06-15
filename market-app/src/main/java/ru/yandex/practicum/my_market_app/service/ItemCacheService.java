package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ItemCacheService {

    private final ReactiveRedisTemplate<String, ItemInfoDto> redisTemplate;
    private final ItemDao itemDao;

    private static final String REDIS_PREFIX = "itemInfo:";

    private static final Duration expire = Duration.of(1, ChronoUnit.MINUTES);

    public Mono<List<ItemInfoDto>> getCachedItems(List<Long> idList) {
        return Mono.fromCallable(() -> idList
                        .stream().map(id -> REDIS_PREFIX + id.toString()).toList())
                .flatMap(keyList -> redisTemplate
                        .opsForValue().multiGet(keyList))
                .map(items -> items.stream().filter(Objects::nonNull).toList());
    }


    public Mono<ItemInfoDto> cacheItem(ItemInfoDto item) {
        return redisTemplate.opsForValue()
                .set(REDIS_PREFIX + item.getId().toString(), item, expire)
                .doOnNext(isSaved -> log.info("cache item: {}", item))
                .thenReturn(item);
    }

    public Mono<ItemInfoDto> getCachedItem(Long id) {
        return redisTemplate.opsForValue().getAndExpire(REDIS_PREFIX + id.toString(), expire)
                .doOnNext(item -> log.info("found in cache: {}", item));
    }

    public Mono<List<ItemFullDto>> collectAllItems(List<Long> idList,
                                                   List<CartItem> cartItems) {

        Mono<List<ItemInfoDto>> cachedItemsMono = this.getCachedItems(idList);

        Mono<List<ItemInfoDto>> missedItemsMono = cachedItemsMono
                .flatMap(cachedItems -> getMissedItems(cachedItems, idList))
                .doOnNext(el -> log.info("missedItem: {}", el));

        return Mono.zip(cachedItemsMono, missedItemsMono)
                .map(tuple ->
                        unionAndEnrichWithAmount(idList, cartItems, tuple.getT1(), tuple.getT2())
                );
    }

    private List<ItemFullDto> unionAndEnrichWithAmount(
            List<Long> idList,
            List<CartItem> cartItems,
            List<ItemInfoDto> cachedItems,
            List<ItemInfoDto> missedItems) {

        List<ItemInfoDto> allItems = Stream.concat(cachedItems.stream(), missedItems.stream()).toList();

        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(CartItem::getItemId, Function.identity()));

        Map<Long, ItemInfoDto> itemsMap = allItems.stream()
                .collect(Collectors.toMap(ItemInfoDto::getId, Function.identity()));

        return idList.stream()
                .map(id -> {
                    ItemInfoDto item = itemsMap.get(id);
                    Integer count = Optional.ofNullable(cartItemMap.get(id))
                            .map(CartItem::getCount).orElse(0);

                    return new ItemFullDto(item, count);
                }).toList();
    }

    private Mono<List<ItemInfoDto>> getMissedItems(List<ItemInfoDto> cachedItems,
                                                   List<Long> idList
    ) {

        Set<Long> cachedIds = cachedItems.stream()
                .filter(Objects::nonNull)
                .map(ItemInfoDto::getId).collect(Collectors.toSet());

        List<Long> missedIds = idList.stream()
                .filter(id -> !cachedIds.contains(id))
                .toList();

        if (missedIds.isEmpty()) {
            return Mono.just(List.of());
        }
        return itemDao.getItemsByIdList(missedIds)
                .flatMap(this::cacheItem)
                .collectList();
    }


}
