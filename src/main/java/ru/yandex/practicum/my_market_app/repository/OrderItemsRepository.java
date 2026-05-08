package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.my_market_app.model.OrderItems;

import java.util.List;

@Repository
public interface OrderItemsRepository extends CrudRepository<OrderItems, Long> {

    
    List<OrderItems> getOrderItemsByItem_Id(Long itemId);

    @Modifying
    @Query("update order_items set count = count + :count where order_id = :orderId and item_id = :itemId")
    int changeItemAmount(Long orderId, Long itemId, Integer count);
    
}
