package ru.yandex.practicum.my_market_app.api;

import ru.yandex.practicum.my_market_app.client.ApiClient;

import ru.yandex.practicum.my_market_app.model.dto.Balance;
import ru.yandex.practicum.my_market_app.model.dto.ChargeBalanceRequest;
import ru.yandex.practicum.my_market_app.model.dto.ChargeStatus;
import ru.yandex.practicum.my_market_app.model.dto.Error;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-06-06T15:32:35.378501+03:00[Europe/Moscow]", comments = "Generator version: 7.22.0")
public class DefaultApi {
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Списание суммы заказа со счета
     * 
     * <p><b>200</b> - Успешное списание средств
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @param chargeBalanceRequest Сумма для списания
     * @return ChargeStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec chargeBalanceRequestCreation(@jakarta.annotation.Nonnull ChargeBalanceRequest chargeBalanceRequest) throws WebClientResponseException {
        Object postBody = chargeBalanceRequest;
        // verify the required parameter 'chargeBalanceRequest' is set
        if (chargeBalanceRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'chargeBalanceRequest' when calling chargeBalance", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ChargeStatus> localVarReturnType = new ParameterizedTypeReference<ChargeStatus>() {};
        return apiClient.invokeAPI("/chargeBalance", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Списание суммы заказа со счета
     * 
     * <p><b>200</b> - Успешное списание средств
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @param chargeBalanceRequest Сумма для списания
     * @return ChargeStatus
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ChargeStatus> chargeBalance(@jakarta.annotation.Nonnull ChargeBalanceRequest chargeBalanceRequest) throws WebClientResponseException {
        ParameterizedTypeReference<ChargeStatus> localVarReturnType = new ParameterizedTypeReference<ChargeStatus>() {};
        return chargeBalanceRequestCreation(chargeBalanceRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Списание суммы заказа со счета
     * 
     * <p><b>200</b> - Успешное списание средств
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @param chargeBalanceRequest Сумма для списания
     * @return ResponseEntity&lt;ChargeStatus&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ChargeStatus>> chargeBalanceWithHttpInfo(@jakarta.annotation.Nonnull ChargeBalanceRequest chargeBalanceRequest) throws WebClientResponseException {
        ParameterizedTypeReference<ChargeStatus> localVarReturnType = new ParameterizedTypeReference<ChargeStatus>() {};
        return chargeBalanceRequestCreation(chargeBalanceRequest).toEntity(localVarReturnType);
    }

    /**
     * Списание суммы заказа со счета
     * 
     * <p><b>200</b> - Успешное списание средств
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @param chargeBalanceRequest Сумма для списания
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec chargeBalanceWithResponseSpec(@jakarta.annotation.Nonnull ChargeBalanceRequest chargeBalanceRequest) throws WebClientResponseException {
        return chargeBalanceRequestCreation(chargeBalanceRequest);
    }

    /**
     * Получение текущего баланса
     * 
     * <p><b>200</b> - Возвращает текущий баланс
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @return Balance
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getBalanceRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Balance> localVarReturnType = new ParameterizedTypeReference<Balance>() {};
        return apiClient.invokeAPI("/balance", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Получение текущего баланса
     * 
     * <p><b>200</b> - Возвращает текущий баланс
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @return Balance
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Balance> getBalance() throws WebClientResponseException {
        ParameterizedTypeReference<Balance> localVarReturnType = new ParameterizedTypeReference<Balance>() {};
        return getBalanceRequestCreation().bodyToMono(localVarReturnType);
    }

    /**
     * Получение текущего баланса
     * 
     * <p><b>200</b> - Возвращает текущий баланс
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @return ResponseEntity&lt;Balance&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Balance>> getBalanceWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<Balance> localVarReturnType = new ParameterizedTypeReference<Balance>() {};
        return getBalanceRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Получение текущего баланса
     * 
     * <p><b>200</b> - Возвращает текущий баланс
     * <p><b>400</b> - Некорректный запрос
     * <p><b>5XX</b> - Ошибки сервера
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getBalanceWithResponseSpec() throws WebClientResponseException {
        return getBalanceRequestCreation();
    }
}
