package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.config.TestSecurityUnitConfig;
import ru.yandex.practicum.my_market_app.controller.CreateItemController;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.service.ImageService;
import ru.yandex.practicum.my_market_app.service.ItemService;
import ru.yandex.practicum.my_market_app.util.WithCustomOidcUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("controller")
@Tag("unit")
@WebFluxTest(CreateItemController.class)
@Import(TestSecurityUnitConfig.class)
@WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
class CreateItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void getCreateItemForm() {

        webTestClient.get().uri("/items/create")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<span class=\"badge text-bg-success\">Создание товара</span>"));
                });

    }

    @Test
    void createItem(){

        String filename = "image.jpg";
        Long createdId = 1L;

        when(imageService.uploadImage(any(Mono.class))).thenReturn(Mono.just(filename));
        when(itemService.createItem(any(ItemFullDto.class))).thenReturn(Mono.just(createdId));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", "test-content".getBytes())
                .filename("test-image.jpg")
                .contentType(MediaType.IMAGE_JPEG);

        builder.part("title", "Test Item");
        builder.part("description", "Test Description");
        builder.part("price", "100");
        builder.part("count", "5");


        webTestClient.post()
                .uri("/items/create")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .value("Location", matcher -> matcher.equals("/items/1"));

    }


}
