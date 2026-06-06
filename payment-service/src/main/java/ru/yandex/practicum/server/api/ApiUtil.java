package ru.yandex.practicum.server.api;

import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiUtil {
    public static Mono<Void> getExampleResponse(ServerWebExchange exchange, MediaType mediaType, String example) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(mediaType);

        byte[] exampleBytes = example.getBytes(StandardCharsets.UTF_8);
        DefaultDataBuffer data = new DefaultDataBufferFactory().wrap(exampleBytes);
        return response.writeWith(Mono.just(data));
    }
}
