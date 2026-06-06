package ru.yandex.practicum.server.unit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.api.AccountApiController;
import ru.yandex.practicum.server.domain.Balance;
import ru.yandex.practicum.server.domain.ChargeBalanceRequest;
import ru.yandex.practicum.server.domain.ChargeStatus;
import ru.yandex.practicum.server.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@Tag("controller")
@WebFluxTest(AccountApiController.class)
class AccountApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountService accountService;

    @Test
    void getBalance() {

        when(accountService.getCurrentBalance()).thenReturn(new Balance(10L));

        webTestClient.get().uri("/balance")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Balance.class)
                .value(balance -> assertEquals(10L, balance.getSum()));
    }

    @Test
    void chargeBalance() {

        ChargeBalanceRequest request = new ChargeBalanceRequest(10L);

        when(accountService.chargeBalance(any(Mono.class)))
                .thenReturn(Mono.empty());

        webTestClient.post().uri("/chargeBalance")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ChargeStatus.class)
                .value(status -> assertEquals("Оплата прошла успешно", status.getStatus()));
    }

}

