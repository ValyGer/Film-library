package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
public class Film implements Comparable<Film> {
    private int id; // идентификатор фильма
    private String name; // название фильма
    private String description; // описание фильма
    private LocalDate releaseDate; // дата выхода фильма
    private Duration duration; //продолжительность фильма
    private Set<Integer> likes = new TreeSet<>();  // список лайков
    private Rating mpa; // рейтинг фильма
    private List<Genre> genres = new ArrayList<>(); // жанры фильма


    public Film(String name, String description, LocalDate releaseDate, Long second) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Duration.ofSeconds(second);
    }

    public Film(int id, String name, String description, LocalDate releaseDate, Long second) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Duration.ofSeconds(second);
    }

    public Film(int id, String name, String description, Duration duration, LocalDate releaseDate, Rating mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, Duration duration, Set<Integer> likes, Rating mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Long getDuration() {
        return duration.toSeconds();
    }

    @Override
    public int compareTo(Film film) {
        return (film.likes.size() - this.likes.size());
    }
}
