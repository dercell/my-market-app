package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemDetailDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.dao.ItemDao;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {

    private final CartService cartService;
    private final ItemDao itemDao;


    public Mono<ItemPageDto> getItemsPage(String search, int pageNumber, int pageSize, String sort) {
        log.info("search: {}, pageNumber: {}, pageSize: {}, sort: {}", search, pageNumber, pageSize, sort);
        Mono<PageDto> paging = itemDao.getTotalRows(search)
                .map(total -> {
                    int totalPages = (int) Math.ceil((double) total / pageSize);
                    boolean hasNext = pageNumber < totalPages - 1;
                    boolean hasPrevious = pageNumber > 0;
                    return new PageDto(pageNumber, pageSize, hasPrevious, hasNext);
                });

        Mono<List<ItemDetailDto>> pageItems = itemDao.getItemPage(search, pageNumber, pageSize, sort)
                .collectList();

        return Mono.zip(pageItems, paging)
                .map(tuple2 ->
                        new ItemPageDto(cutItems(tuple2.getT1()), search, sort, tuple2.getT2()));

    }

    public Mono<ItemDetailDto> getItem(Long id) {
        return itemDao.getItem(id);
    }

    public Mono<Void> changeItemAmount(Long itemId, String action) {
        return cartService.changeItemAmount(itemId, action).then();
    }

    public Mono<Long> createItem(ItemDetailDto itemDetailDto) {
        return itemDao.createItem(itemDetailDto);
    }

    private List<List<ItemDetailDto>> cutItems(List<ItemDetailDto> itemPage) {
        List<List<ItemDetailDto>> result = new ArrayList<>();
        List<ItemDetailDto> chunk = new ArrayList<>(3);

        for (ItemDetailDto i : itemPage) {
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
