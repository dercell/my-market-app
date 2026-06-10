package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ItemCacheService {

    private final ReactiveRedisTemplate<String, ItemInfoDto> redisTemplate;

    private static final String REDIS_PREFIX = "itemInfo:";

    private static final Duration expire = Duration.of(1, ChronoUnit.MINUTES);

    public Mono<List<ItemInfoDto>> getCachedItems(List<Long> idList) {
        return Mono.fromCallable(() -> idList
                        .stream().map(id -> REDIS_PREFIX + id.toString()).toList())
                .flatMap(keyList -> redisTemplate
                        .opsForValue().multiGet(keyList))
                .map(items -> items.stream().filter(Objects::nonNull).toList());
    }


    public Mono<ItemInfoDto> cacheItem(ItemInfoDto item) {
        log.info("cache item:" + item);
        return redisTemplate.opsForValue().set(REDIS_PREFIX + item.getId().toString(), item, expire)
                .thenReturn(item);
    }

    public Mono<ItemInfoDto> getCachedItem(Long id) {
        return redisTemplate.opsForValue().getAndExpire(REDIS_PREFIX + id, expire);
    }


}
