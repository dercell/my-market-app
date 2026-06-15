package ru.yandex.practicum.my_market_app.model.dto.payment;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentAvailability {

    private boolean isAvailable;
    private String message;

}
