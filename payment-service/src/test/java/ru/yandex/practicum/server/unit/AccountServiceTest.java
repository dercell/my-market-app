package ru.yandex.practicum.server.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.domain.ChargeBalanceRequest;
import ru.yandex.practicum.server.exception.InvalidSumValueException;
import ru.yandex.practicum.server.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(100000L);
    }

    @Test
    void getCurrentBalanace() {
        assertEquals(100000L, accountService.getCurrentBalance().getSum());
    }

    @Test
    void chargeBalance() {
        ChargeBalanceRequest request = new ChargeBalanceRequest(50000L);
        accountService.chargeBalance(Mono.just(request)).block();

        assertEquals(50000L, accountService.getCurrentBalance().getSum());
    }

    @Test
    void invalidCharge() {
        ChargeBalanceRequest request = new ChargeBalanceRequest(-12L);

        assertThrows(InvalidSumValueException.class, () -> accountService.chargeBalance(Mono.just(request)).block());
    }

}
