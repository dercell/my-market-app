package ru.yadex.practicum.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yadex.practicum.server.domain.Balance;
import ru.yadex.practicum.server.domain.ChargeBalanceRequest;
import ru.yadex.practicum.server.domain.ChargeStatus;
import ru.yadex.practicum.server.domain.Error;
import ru.yadex.practicum.server.exception.NotEnoughMoneyException;
import ru.yadex.practicum.server.service.AccountService;

@Controller
@RequestMapping("${openapi.aPI.base-path:}")
@Validated
@AllArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    /**
     * GET /balance : Получение текущего баланса
     *
     * @return Возвращает текущий баланс (status code 200)
     * or Некорректный запрос (status code 400)
     * or Ошибки сервера (status code 5XX)
     */
    @Operation(
            operationId = "getBalance",
            summary = "Получение текущего баланса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Возвращает текущий баланс", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Balance.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ru.yadex.practicum.server.domain.Error.class))
                    }),
                    @ApiResponse(responseCode = "5XX", description = "Ошибки сервера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @Tag(name = "balance", description = "the balance API")
    @GetMapping(
            value = "/balance",
            produces = {"application/json"}
    )
    Mono<ResponseEntity<Balance>> getBalance(
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            String exampleString = "";

            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"balance\" : 15000 }";
            }

            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"message\" : \"Сообщение об ошибке\" }";
            }

            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"message\" : \"Сообщение об ошибке\" }";
            }

            ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
        }

        return Mono.just(ResponseEntity.ok(accountService.getCurrentBalance()));

    }


    /**
     * POST /chargeBalance : Списание суммы заказа со счета
     *
     * @param chargeBalanceRequest Сумма для списания (required)
     * @return Успешное списание средств (status code 200)
     * or Некорректный запрос (status code 400)
     * or Ошибки сервера (status code 5XX)
     */
    @Operation(
            operationId = "chargeBalance",
            summary = "Списание суммы заказа со счета",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное списание средств", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ChargeStatus.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ru.yadex.practicum.server.domain.Error.class))
                    }),
                    @ApiResponse(responseCode = "5XX", description = "Ошибки сервера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @Tag(name = "chargeBalance", description = "the chargeBalance API")
    @PostMapping(
            value = "/chargeBalance",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    Mono<ResponseEntity<ChargeStatus>> chargeBalance(
            @Parameter(name = "ChargeBalanceRequest", description = "Сумма для списания", required = true) @Valid @RequestBody Mono<ChargeBalanceRequest> chargeBalanceRequest,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        String exampleString = "";
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"status\" : \"Оплата прошла успешно\", \"isSuccess\" : true }";
            }

            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"message\" : \"Сообщение об ошибке\" }";
            }

            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                exampleString = "{ \"message\" : \"Сообщение об ошибке\" }";
            }

            ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
        }

        return accountService.chargeBalance(chargeBalanceRequest)
                .then(Mono.just(new ChargeStatus("Оплата прошла успешно", true)))
                .onErrorResume(NotEnoughMoneyException.class, ex -> Mono.just(new ChargeStatus(ex.getMessage(), false)))
                .map(ResponseEntity::ok);
    }

}
