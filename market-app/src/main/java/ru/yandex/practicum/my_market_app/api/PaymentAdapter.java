package ru.yandex.practicum.my_market_app.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.my_market_app.model.dto.payment.Balance;
import ru.yandex.practicum.my_market_app.model.dto.payment.ChargeBalanceRequest;
import ru.yandex.practicum.my_market_app.model.dto.payment.ChargeStatus;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentAdapter {

    private final WebClient webClient;

    /**
     * Списание суммы заказа со счета
     *
     * <p><b>200</b> - Успешное списание средств
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     *
     * @param chargeBalanceRequest Сумма для списания
     * @return ChargeStatus
     */
    public Mono<ChargeStatus> chargeBalance(ChargeBalanceRequest chargeBalanceRequest) {
        return webClient.post().uri("/chargeBalance")
                .bodyValue(chargeBalanceRequest)
                .retrieve()
                .bodyToMono(ChargeStatus.class);
    }


    /**
     * Получение текущего баланса
     *
     * <p><b>200</b> - Возвращает текущий баланс
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     *
     * @return Balance
     *
     */
    public Mono<Balance> getBalance() {
        return webClient.get().uri("/balance")
                .retrieve()
                .bodyToMono(Balance.class);
    }


}
