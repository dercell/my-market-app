package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.api.PaymentAdapter;
import ru.yandex.practicum.my_market_app.model.dto.payment.Balance;
import ru.yandex.practicum.my_market_app.model.dto.payment.ChargeBalanceRequest;
import ru.yandex.practicum.my_market_app.model.dto.payment.ChargeStatus;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;
import ru.yandex.practicum.my_market_app.service.PaymentService;
import ru.yandex.practicum.my_market_app.util.exception.PaymentServiceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentAdapter paymentAdapter;

    @ParameterizedTest
    @CsvSource({"2, 1, true", "0, 1, false"})
    void checkBalance(long balance, long totalSum, boolean isAvailable) {
        Balance b = new Balance(balance);
        when(paymentAdapter.getBalance()).thenReturn(Mono.just(b));
        PaymentAvailability pa = paymentService.checkBalance(totalSum).block();

        assertEquals(pa.isAvailable(), isAvailable);
    }

    @Test
    void chargeOrderBalance() {
        ChargeBalanceRequest request = new ChargeBalanceRequest(1L);
        ChargeStatus status = new ChargeStatus("Успех", true);
        when(paymentAdapter.chargeBalance(request)).thenReturn(Mono.just(status));

        Long orderId = paymentService.chargeOrderBalance(1L, 1L).block();

        assertEquals(1L, orderId);
    }

    @Test
    void failCharge() {
        ChargeBalanceRequest request = new ChargeBalanceRequest(1L);
        ChargeStatus status = new ChargeStatus("Ошибка", false);
        when(paymentAdapter.chargeBalance(request)).thenReturn(Mono.just(status));

        assertThrows(PaymentServiceException.class, () -> paymentService.chargeOrderBalance(1L, 1L).block());


    }

}
