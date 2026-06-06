package ru.yadex.practicum.server.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${openapi.aPI.base-path:}")
public class BalanceApiController implements BalanceApi {

}
