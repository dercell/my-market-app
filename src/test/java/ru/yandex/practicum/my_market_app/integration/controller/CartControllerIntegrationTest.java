package ru.yandex.practicum.my_market_app.integration.controller;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("view")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@Sql(statements = "insert into cart(id, item_id, count) values (1,1,1), (2,2,2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(statements = "truncate table cart", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(value = "classpath:db/scripts/clear_orders.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCartItems() throws Exception {

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(model().attribute("total", is(27000L)));


    }

    @Test
    void changeItemAmount() throws Exception {

        mockMvc.perform(post("/cart/items")
                        .param("id", "2")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(model().attribute("total", is(38000L)));
    }

    @Test
    void buy() throws Exception {

        mockMvc.perform(post("/cart/items/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/1?newOrder=true"))
                .andReturn();
    }

}
