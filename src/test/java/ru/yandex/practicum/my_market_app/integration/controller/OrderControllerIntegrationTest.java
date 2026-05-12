package ru.yandex.practicum.my_market_app.integration.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;

import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@Sql(value = "classpath:db/scripts/init_orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "classpath:db/scripts/clear_orders.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOrders() throws Exception {

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", hasSize(2)));
    }

    @Test
    void getOrderDetail() throws Exception {

        MvcResult result = mockMvc.perform(get("/orders/{id}", 2L)
                        .param("newOrder", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andReturn();

        OrderPageDto orderPageDto = (OrderPageDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("order");
        assertEquals(15000L, orderPageDto.totalSum());
        assertEquals(2, orderPageDto.items().size());
    }


}
