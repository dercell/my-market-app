package ru.yandex.practicum.my_market_app;

import org.springframework.boot.SpringApplication;
import ru.yandex.practicum.my_market_app.config.MyTestContainers;

public class TestMyMarketAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(MyMarketAppApplication::main).with(MyTestContainers.class).run(args);
	}

}
