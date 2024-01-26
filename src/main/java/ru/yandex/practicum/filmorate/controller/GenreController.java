package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@AllArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenre() {
        return genreService.getAllGenre();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Integer genreId) {
        return genreService.getGenreById(genreId);
    }
}
