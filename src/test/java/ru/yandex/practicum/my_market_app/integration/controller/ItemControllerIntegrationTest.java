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
import ru.yandex.practicum.my_market_app.model.dto.PageDto;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("view")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItemPage() throws Exception {

        MvcResult result = mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(model().attribute("search", is("")))
                .andExpect(model().attribute("sort", is("NO")))
                .andReturn();

        Map<String, Object> model = Objects.requireNonNull(result.getModelAndView()).getModel();
        PageDto paging = (PageDto) model.get("paging");
        assertEquals(0, paging.pageNumber());
        assertEquals(5, paging.pageSize());

    }


    @Test
    void changeItemAmount() throws Exception {

        mockMvc.perform(post("/items")
                        .param("id", "1")
                        .param("search", "")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("sort", "NO")
                        .param("action", "PLUS")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/items?search=&sort=NO&pageNumber=0&pageSize=5"));

    }

}
