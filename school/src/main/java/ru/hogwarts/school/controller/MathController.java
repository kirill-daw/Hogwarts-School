package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.MathService;

@RestController
@RequestMapping("/math")
public class MathController {

    private final MathService mathService;

    public MathController(MathService mathService) {
        this.mathService = mathService;
    }

    @GetMapping("/sum-slow")
    public long getSumSlow() {
        return mathService.calculateSumSlow();
    }

    @GetMapping("/sum-formula")
    public long getSumFormula() {
        return mathService.calculateSumFormula();
    }

    @GetMapping("/sum-parallel")
    public long getSumParallel() {
        return mathService.calculateSumParallel();
    }

    @GetMapping("/sum-range")
    public long getSumRange() {
        return mathService.calculateSumRange();
    }
}