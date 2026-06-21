package ru.yandex.practicum.my_market_app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;import reactor.core.publisher.Mono;import ru.yandex.practicum.my_market_app.model.entity.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> getUsersByLogin(String login);

}
