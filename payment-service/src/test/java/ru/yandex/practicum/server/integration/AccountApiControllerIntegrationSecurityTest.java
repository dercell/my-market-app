package ru.yandex.practicum.server.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.server.domain.Balance;
import ru.yandex.practicum.server.domain.ChargeBalanceRequest;
import ru.yandex.practicum.server.domain.ChargeStatus;
import ru.yandex.practicum.server.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("controller")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountApiControllerIntegrationSecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService.getCurrentBalance().setBalance(100000L);
    }

    @Test
    @WithMockUser(authorities = {"SERVICE"})
    void getBalance() {
        webTestClient.get().uri("/balance")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Balance.class)
                .value(balance -> assertEquals(100000L, balance.getBalance()));
    }

    @Test
    void getBalanceUnauthorized() {
        webTestClient.get().uri("/balance")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = {"OTHER_SERVICE"})
    void getBalanceForbidden() {
        webTestClient.get().uri("/balance")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(authorities = {"SERVICE"})
    void chargeBalance() {

        ChargeBalanceRequest request = new ChargeBalanceRequest(10L);

        webTestClient.post().uri("/chargeBalance")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ChargeStatus.class)
                .value(status -> assertEquals("Оплата прошла успешно", status.getStatus()));
    }

    @Test
    void chargeBalanceUnauthorized() {

        ChargeBalanceRequest request = new ChargeBalanceRequest(10L);

        webTestClient.post().uri("/chargeBalance")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = {"OTHER_SERVICE"})
    void chargeForbidden() {

        ChargeBalanceRequest request = new ChargeBalanceRequest(10L);

        webTestClient.post().uri("/chargeBalance")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isForbidden();
    }



}
