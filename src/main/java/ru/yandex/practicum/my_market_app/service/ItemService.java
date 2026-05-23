package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.repository.ItemDao;
import java.util.*;


@Service
@AllArgsConstructor
public class ItemService {

    private final CartService cartService;
    private final ItemDao itemDao;


    public Mono<ItemPageDto> getItemsPage(String search, int pageNumber, int pageSize, String sort) {

        Mono<PageDto> paging = itemDao.getTotalRows(search)
                .map(total -> {
                    int totalPages = (int) Math.ceil((double) total / pageSize);
                    boolean hasNext = pageNumber < totalPages;
                    boolean hasPrevious = pageNumber > 0;
                    return new PageDto(pageNumber, pageSize, hasPrevious, hasNext);
                });

        Mono<List<ItemDto>> pageItems = itemDao.getItemPage(search, pageNumber, pageSize, sort)
                .collectList();

        return Mono.zip(pageItems, paging)
                .map(tuple2 ->
                        new ItemPageDto(cutItems(tuple2.getT1()), search, sort, tuple2.getT2()));

    }

    public Mono<ItemDto> getItem(Long id) {
        return itemDao.getItem(id);
    }

    public void changeItemAmount(Long itemId, String action) {
        cartService.changeItemAmount(itemId, action);
    }

    private List<List<ItemDto>> cutItems(List<ItemDto> itemPage) {
        List<List<ItemDto>> result = new ArrayList<>();
        List<ItemDto> chunk = new ArrayList<>(3);

        for (ItemDto i : itemPage) {
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
