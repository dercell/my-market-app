package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.api.PaymentAdapter;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;

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

}
