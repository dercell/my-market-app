package ru.yandex.practicum.my_market_app.model.dto;

import lombok.Data;

@Data
public class ItemForm {
    private Long id;
    private String action;
    private String search = "";
    private String sort = "NO";
    private int pageSize = 5;
    private  int pageNumber = 0;
}
