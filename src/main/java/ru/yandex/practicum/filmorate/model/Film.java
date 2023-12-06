package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Film {
    private int id; // идентификатор фильма
    private String name; // название фильма
    private String description; // описание фильма
    private LocalDate releaseDate; // дата выхода фильма
    private Duration duration; //продолжительность фильма

    public Film(String name, String description, LocalDate releaseDate, Long second) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Duration.ofSeconds(second);
    }

    public Long getDuration() {
        return duration.toSeconds();
    }
}
