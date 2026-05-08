package ru.yandex.practicum.my_market_app.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.my_market_app.model.Item;
import ru.yandex.practicum.my_market_app.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    List<List<Item>> getItemsPage(String search, Pageable page, Sort sort) {
        List<List<Item>> result = new ArrayList<>();
        List<Item> chunk = new ArrayList<>(3);
        List<Item> itemList = itemRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, page, sort);
        for (Item i : itemList) {
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

    Optional<Item> getItem(Long id) {
        return itemRepository.findById(id);
    }



}
