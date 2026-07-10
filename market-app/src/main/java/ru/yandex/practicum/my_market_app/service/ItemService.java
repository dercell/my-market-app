package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {

    private final CartService cartService;
    private final ItemDao itemDao;
    private final ItemCacheService itemCacheService;


    public Mono<ItemFullDto> getItem(Long id, Long userId) {
        return itemCacheService.getCachedItem(id)
                .switchIfEmpty(loadFromDatabaseAndCache(id))
                .flatMap(itemInfoDto -> Mono.just(userId)
                        .filter(uid -> uid != -1L)
                        .flatMap(uid -> cartService.getCartItemByItemIdAndUserId(id, uid)
                                .map(CartItem::getCount)
                                .defaultIfEmpty(0))
                        .switchIfEmpty(Mono.just(0))
                        .map(count -> new ItemFullDto(itemInfoDto, count))
                );
    }

    private Mono<ItemInfoDto> loadFromDatabaseAndCache(Long id) {
        return itemDao.getItem(id)
                .doOnNext(item -> log.info("load item from database {} ", item))
                .flatMap(itemInfoDto -> itemCacheService
                        .cacheItem(itemInfoDto)
                        .thenReturn(itemInfoDto));
    }

    public Mono<Void> changeItemAmount(Long itemId, String action, Long userId) {
        return cartService.changeItemAmount(itemId, action, userId).then();
    }

    public Mono<Long> createItem(ItemFullDto itemFullDto) {
        return itemDao.createItem(itemFullDto);
    }

    public Mono<ItemPageDto> getItemsPage(Long userId, String search, int pageNumber, int pageSize, String sort) {
        log.info("search: {}, pageNumber: {}, pageSize: {}, sort: {}", search, pageNumber, pageSize, sort);

        return itemDao.getTotalRows(search)
                .flatMap(total -> {
                    int totalPages = (int) Math.ceil((double) total / pageSize);
                    boolean hasNext = pageNumber < totalPages - 1;
                    boolean hasPrevious = pageNumber > 0;
                    PageDto paging = new PageDto(pageNumber, pageSize, hasPrevious, hasNext);
                    return itemDao.getItemIdsPage(search, pageNumber, pageSize, sort)
                            .collectList()
                            .flatMap(idList -> Mono.just(userId).filter(uid -> uid != -1L)
                                    .flatMap(uid -> cartService.getCartItemsByIdList(idList, uid).collectList())
                                    .flatMap(cartItemsList -> itemCacheService
                                            .collectAllItems(idList, cartItemsList)
                                    ).switchIfEmpty(itemCacheService
                                            .collectAllItems(idList, List.of()))
                            )
                            .map(allItems -> new ItemPageDto(cutItems(allItems),
                                    search, sort, paging));
                });
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
