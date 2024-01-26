package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<Rating> getAllRating() {
        return ratingService.getAllRating();
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable("id") Integer ratingId) {
        return ratingService.getRatingById(ratingId);
    }
}
