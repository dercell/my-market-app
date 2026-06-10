package ru.yandex.practicum.my_market_app.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;


@Configuration
public class RedisConfig {


    @Bean
    public ReactiveRedisTemplate<String, ItemInfoDto> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        JsonMapper jm = new JsonMapper();
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        JacksonJsonRedisSerializer<ItemInfoDto> valueSerializer = new JacksonJsonRedisSerializer<>(jm, ItemInfoDto.class);

        RedisSerializationContext<String, ItemInfoDto> context =
                RedisSerializationContext.<String, ItemInfoDto>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}
