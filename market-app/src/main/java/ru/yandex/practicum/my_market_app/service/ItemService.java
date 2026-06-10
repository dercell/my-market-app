package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {

    private final CartService cartService;
    private final ItemDao itemDao;
    private final ReactiveRedisTemplate<String, ItemInfoDto> redisTemplate;

    private static final String REDIS_PREFIX = "itemInfo:";

    public Mono<ItemPageDto> getItemsPage(String search, int pageNumber, int pageSize, String sort) {
        log.info("search: {}, pageNumber: {}, pageSize: {}, sort: {}", search, pageNumber, pageSize, sort);

        return itemDao.getTotalRows(search)
                .flatMap(total -> {
                    int totalPages = (int) Math.ceil((double) total / pageSize);
                    boolean hasNext = pageNumber < totalPages - 1;
                    boolean hasPrevious = pageNumber > 0;
                    PageDto paging = new PageDto(pageNumber, pageSize, hasPrevious, hasNext);

                    return itemDao.getItemIdsPage(search, pageNumber, pageSize, sort)
                            .collectList()
                            .flatMap(this::collectAllItems)
                            .map(allItems -> new ItemPageDto(cutItems(allItems),
                                    search, sort, paging));
                });
    }


    public Mono<ItemFullDto> getItem(Long id) {
        return itemDao.getItem(id);
    }

    public Mono<Void> changeItemAmount(Long itemId, String action) {
        return cartService.changeItemAmount(itemId, action).then();
    }

    public Mono<Long> createItem(ItemFullDto itemFullDto) {
        return itemDao.createItem(itemFullDto);
    }

    private Mono<List<ItemFullDto>> collectAllItems(List<Long> idList) {
        Mono<List<CartItem>> cartItemsMono = cartService.getCartItemsByIdList(idList).collectList();

        Mono<List<ItemInfoDto>> cachedItemsMono = Mono.fromCallable(() -> idList
                        .stream().map(id -> REDIS_PREFIX + id).toList())
                .flatMap(keyList -> redisTemplate
                        .opsForValue().multiGet(keyList));

        Mono<List<ItemInfoDto>> missedItemsMono = cachedItemsMono
                .flatMap(cachedItems -> getMissedItems(cachedItems, idList));

        return Mono.zip(cartItemsMono, cachedItemsMono, missedItemsMono)
                .map(tuple ->
                        unionAndEnrichWithAmount(idList, tuple.getT1(), tuple.getT2(), tuple.getT3()));
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
                .map(ItemInfoDto::getId).collect(Collectors.toSet());

        List<Long> missedIds = idList.stream()
                .filter(id -> !cachedIds.contains(id))
                .toList();

        if (missedIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return itemDao.getItemsByIdList(missedIds)
                .doOnNext(item -> redisTemplate
                        .opsForValue()
                        .set(REDIS_PREFIX + item.getId(), item)
                ).collectList();
    }

    private List<List<ItemFullDto>> cutItems(List<ItemFullDto> itemPage) {
        List<List<ItemFullDto>> result = new ArrayList<>();
        List<ItemFullDto> chunk = new ArrayList<>(3);

        for (ItemFullDto i : itemPage) {
            if (chunk.size() == 3) {
                result.add(new ArrayList<>(chunk));
                chunk.clear();
            }
            chunk.add(i);
        }
        if (!chunk.isEmpty()) {
            result.add(new ArrayList<>(chunk));
        }
        return result;
    }
}
