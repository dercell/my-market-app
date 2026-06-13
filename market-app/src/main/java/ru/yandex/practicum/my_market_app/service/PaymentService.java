package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.api.PaymentAdapter;
import ru.yandex.practicum.my_market_app.model.dto.payment.ChargeBalanceRequest;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;
import ru.yandex.practicum.my_market_app.util.exception.PaymentServiceException;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentAdapter paymentAdapter;

    public Mono<PaymentAvailability> checkBalance(long totalSum) {

        PaymentAvailability paymentAvailability = new PaymentAvailability(false, "Сервис недоступен");
        return paymentAdapter.getBalance()
                .map(balance -> {
                    if (balance.getSum() >= totalSum) {
                        paymentAvailability.setAvailable(true);
                        paymentAvailability.setMessage("Всё в порядке");
                    } else {
                        paymentAvailability.setMessage("Недостаточно денег на счёте");
                    }
                    return paymentAvailability;
                })
                .onErrorResume(ex -> {
                    log.info(ex.getMessage());
                    paymentAvailability.setMessage("Сервис оплаты недоступен");
                    return Mono.just(paymentAvailability);
                });
    }

    public Mono<Long> chargeOrderBalance(Long orderId, long totalSum){
        ChargeBalanceRequest request = new ChargeBalanceRequest(totalSum);
        return paymentAdapter.chargeBalance(request)
                .flatMap(chargeStatus -> {
                    if (chargeStatus.getIsSuccess()) {
                        return Mono.just(orderId);
                    } else {
                        return Mono.error(new PaymentServiceException(chargeStatus.getStatus()));
                    }
                });
    }

}
