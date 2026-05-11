package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;

import java.util.List;


@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> getCartItemByItem_Id(Long itemId);

}
