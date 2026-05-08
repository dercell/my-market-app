package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.my_market_app.model.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

     List<Order> getOrderByStatusEqualsIgnoreCase(String status);

     @Modifying
     @Query("update orders set status = 'COMPLETED' where id = :id")
     int closeOrder(Long id);

}
