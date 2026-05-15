package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.entity.Item;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {

    Page<Item> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable page);


}
