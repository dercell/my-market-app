package ru.yandex.practicum.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.domain.Balance;
import ru.yandex.practicum.server.domain.ChargeBalanceRequest;
import ru.yandex.practicum.server.exception.InvalidSumValueException;
import ru.yandex.practicum.server.exception.NotEnoughMoneyException;

@Component
public class AccountService {

    private final Balance CURRENT_BALANCE;

    public AccountService(@Value("${payment-service.current-balance}") Long balance) {
        CURRENT_BALANCE = new Balance(balance);
    }

    public Balance getCurrentBalance() {
        return CURRENT_BALANCE;
    }

    public Mono<Void> chargeBalance(Mono<ChargeBalanceRequest> chargeBalanceRequestMono) {
        return chargeBalanceRequestMono
                .map(ChargeBalanceRequest::getTotalSum)
                .filter(chargeSum -> chargeSum >= 0)
                .switchIfEmpty(Mono.error(new InvalidSumValueException("Некорректно указанная сумма")))
                .filter(chargeSum -> chargeSum <= CURRENT_BALANCE.getSum())
                .switchIfEmpty(Mono.error(new NotEnoughMoneyException("Недостаточно денег на счете")))
                .flatMap(chargeSum -> {
                    CURRENT_BALANCE.setSum(CURRENT_BALANCE.getSum() - chargeSum);
                    return Mono.empty();
                });
    }

}
