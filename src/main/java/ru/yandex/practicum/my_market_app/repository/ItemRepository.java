package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {

    List<Item> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable page, Sort sort);


}
