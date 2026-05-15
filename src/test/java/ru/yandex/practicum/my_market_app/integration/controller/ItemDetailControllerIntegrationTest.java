package ru.yandex.practicum.my_market_app.integration.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;

import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ItemDetailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItem() throws Exception {

        MvcResult result = mockMvc.perform(get("/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andReturn();

        ItemDto itemDto = (ItemDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("item");

        assertEquals("X-Wing", itemDto.title());

    }

    @Test
    void changeItemAmount() throws Exception {

        MvcResult result = mockMvc.perform(post("/items/{id}", 1L)
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andReturn();

        ItemDto itemDto = (ItemDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("item");
        assertEquals(1, itemDto.count());
    }

}
