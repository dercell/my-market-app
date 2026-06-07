package ru.yandex.practicum.my_market_app.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemDetailDto;
import ru.yandex.practicum.my_market_app.util.mappers.ItemMapper;

@Repository
@AllArgsConstructor
public class ItemDao {

    private final R2dbcEntityTemplate template;

    private static final String GET_ITEM_DTO_SQL = """
            select i.id, i.title, i.description, i.price, i.img_path, coalesce(c.count, 0) as count
            from items as i
                     left join cart as c on i.id = c.item_id
            where i.id = :id
            """;

    private static final String GET_PAGE_SQL = """
            select i.id, i.title, i.description, i.price, i.img_path, coalesce(c.count, 0) as count
            from items as i
                     left join cart as c on i.id = c.item_id
            where i.title like :search
               or i.description like :search
            order by :sort
            limit :limit offset :offset
            """;

    private static final String GET_CART_ITEMS_SQL = """
            select i.id, i.title, i.description, i.price, i.img_path, coalesce(c.count, 0) as count
            from items as i join cart as c on i.id = c.item_id
            """;

    private static final String GET_ORDER_ITEMS_SQL = """
            select i.id, i.title, i.description, i.price, i.img_path, coalesce(oi.count, 0) as count
            from items as i join order_items as oi on i.id = oi.item_id
            where oi.order_id = :order_id
            """;

    private static final String GET_COUNT_ITEMS_SQL = """
            select count(*) as cnt from items
            where title like :search or description like :search
            """;

    public Mono<Long> getTotalRows(String search) {
        return template.getDatabaseClient()
                .sql(GET_COUNT_ITEMS_SQL)
                .bind("search", "%" + search + "%")
                .fetch().one().map(row -> (Long) row.get("cnt"));
    }

    public Flux<ItemDetailDto> getItemPage(String search, int pageNumber, int pageSize, String sort) {
        return template.getDatabaseClient()
                .sql(GET_PAGE_SQL)
                .bind("search", "%" + search + "%")
                .bind("limit", pageSize)
                .bind("offset", pageNumber * pageSize)
                .bind("sort", getItemSort(sort))
                .map(ItemMapper.itemDtoRowMapper())
                .all();
    }

    public Mono<ItemDetailDto> getItem(Long itemId) {
        return template.getDatabaseClient()
                .sql(GET_ITEM_DTO_SQL)
                .bind("id", itemId)
                .map(ItemMapper.itemDtoRowMapper())
                .first();
    }

    public Flux<ItemDetailDto> getItemsInCart() {
        return template.getDatabaseClient().sql(GET_CART_ITEMS_SQL)
                .map(ItemMapper.itemDtoRowMapper()).all();
    }

    public Flux<ItemDetailDto> getOrderItems(Long orderId) {
        return template.getDatabaseClient().sql(GET_ORDER_ITEMS_SQL)
                .bind("order_id", orderId)
                .map(ItemMapper.itemDtoRowMapper())
                .all();
    }

    private int getItemSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> 2;
            case "PRICE" -> 4;
            default -> 1;
        };
    }

}
