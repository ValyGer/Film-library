package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;


public interface RatingStorage {

    List<Rating> getAllRating();

    Rating getRatingById(Integer ratingId);
}
