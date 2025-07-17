package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RateDto;
import ru.yandex.practicum.filmorate.service.RateService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Validated
@Slf4j
public class RateController {
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping
    public List<RateDto> findAllRates() {
        return rateService.findAll();
    }

    @GetMapping("/{id}")
    public RateDto getRate(@PathVariable Integer id) {
        return rateService.getRate(id);
    }
}
