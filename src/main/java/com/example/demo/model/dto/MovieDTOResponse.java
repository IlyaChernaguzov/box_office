package com.example.demo.model.dto;

import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTOResponse {

    String nameMovie;
    Integer durationMovie;
    Rating ratingMovie;
    Genre genreMovie;
}
