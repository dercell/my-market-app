package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.repository.ItemRepository;
import ru.yandex.practicum.my_market_app.util.mappers.ItemMapper;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CartService cartService;

    public ItemPageDto getItemsPage(String search, int pageNumber, int pageSize, String sort) {
        Sort sortColumn = getItemSort(sort);
        Pageable page = PageRequest.of(pageNumber, pageSize, sortColumn);

        Page<Item> pageItems = itemRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, page);
        CartPageDto cartPageDto = cartService.getCart();

        Map<Long, ItemDto> itemAmountMap = cartPageDto.itemsList().stream().collect(Collectors.toMap(ItemDto::id, p -> p));

        List<ItemDto> itemDtos = pageItems.getContent().stream()
                .map(item ->
                        itemAmountMap.get(item.getId()) != null ?
                                itemAmountMap.get(item.getId()) : ItemMapper.toDto(item, 0)
                ).toList();

        PageDto paging = new PageDto(pageNumber, pageSize, pageItems.hasPrevious(), pageItems.hasNext());
        return new ItemPageDto(cutItems(itemDtos), search, sort, paging);
    }

    public Optional<ItemDto> getItem(Long id) {
        int amount = cartService.getItemAmount(id);
        return itemRepository.findById(id)
                .map(item -> ItemMapper.toDto(item, amount));
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

    private Sort getItemSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title");
            case "PRICE" -> Sort.by("price");
            default -> Sort.by("id");
        };
    }
}
