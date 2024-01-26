package ru.yandex.practicum.filmorate.service.Impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingDbStorage ratingDbStorage;

    public List<Rating> getAllRating() {
        return ratingDbStorage.getAllRating();
    }

    public Rating getRatingById(Integer ratingId) {
        return ratingDbStorage.getRatingById(ratingId);
    }
}
