package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.controller.ItemController;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Tag("controller")
@Tag("unit")
@WebFluxTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getItemPage() {

        ItemPageDto itemPageDto = new ItemPageDto(
                List.of(List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                        new ItemDto(2L, "item2", "", "", 3, 1))),
                "",
                "NO",
                new PageDto(0, 5, false, false)
        );

        when(itemService.getItemsPage("", 0, 5, "NO"))
                .thenReturn(Mono.just(itemPageDto));

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h5 class=\"card-title\">item1</h5>");
                    assert html.contains("<h5 class=\"card-title\">item2</h5>");
                    assert html.contains("<option value=\"5\" selected=\"selected\">5</option>");
                });
    }

    @Test
    void changeItemAmount() {

        when(itemService.changeItemAmount(anyLong(), anyString())).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", "1")
                        .queryParam("search", "")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "5")
                        .queryParam("sort", "NO")
                        .queryParam("action", "PLUS").build()
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}
