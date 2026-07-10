package ru.yandex.practicum.my_market_app.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;import org.springframework.data.relational.core.mapping.Column;import org.springframework.data.relational.core.mapping.Table;import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column
    @EqualsAndHashCode.Include
    private Long id;

    @Column
    private String login;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column
    private String email;

    @Column("last_login_dttm")
    private LocalDateTime lastLoginDttm;

}
