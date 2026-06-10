package ru.yandex.practicum.my_market_app.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.service.ImageService;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.text.MessageFormat;

@Controller
@AllArgsConstructor
@RequestMapping("/items/create")
public class CreateItemController {

    private final ImageService imageService;
    private final ItemService itemService;

    @GetMapping
    public Mono<Rendering> getCreateItemForm() {
        return Mono.just(Rendering.view("new_item")
                .modelAttribute("item", new ItemInfoDto())
                .build());
    }

    @PostMapping
    public Mono<String> createItem(
            @ModelAttribute @Valid ItemFullDto itemFullDto,
            @RequestPart("image") Mono<FilePart> filePartMono
    ) {
        return imageService.uploadImage(filePartMono)
                .flatMap(filename -> {
                    itemFullDto.setImgPath(filename);
                    return itemService.createItem(itemFullDto);
                }).map(id -> MessageFormat.format("redirect:/items/{0}", id));
    }

}
