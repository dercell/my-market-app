package ru.yandex.practicum.my_market_app.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.entity.Item;

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
            limit :limit offset :offset
            order by :sort
            """;

    public Mono<Long> getTotalRows(String search) {
        return template.select(Item.class).matching(Query.query(Criteria
                        .where("title")
                        .like(search).or(
                                Criteria.where("description").like(search))))
                .count();
    }

    public Flux<ItemDto> getItemPage(String search, int pageNumber, int pageSize, String sort) {
        return template.getDatabaseClient()
                .sql(GET_PAGE_SQL)
                .bind("search", search)
                .bind("limit", pageSize)
                .bind("offset", pageNumber * pageSize)
                .bind("sort", getItemSort(sort))
                .map(row ->
                        new ItemDto(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("description", String.class),
                                row.get("img_path", String.class),
                                row.get("price", Long.class),
                                row.get("count", Integer.class)
                        ))
                .all();
    }

    public Mono<ItemDto> getItem(Long itemId) {
        return template.getDatabaseClient()
                .sql(GET_ITEM_DTO_SQL)
                .bind("id", itemId)
                .map(row ->
                        new ItemDto(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("description", String.class),
                                row.get("img_path", String.class),
                                row.get("price", Long.class),
                                row.get("count", Integer.class)
                        ))
                .first();
    }

    private String getItemSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> "title";
            case "PRICE" -> "price";
            default -> "id";
        };
    }

}
