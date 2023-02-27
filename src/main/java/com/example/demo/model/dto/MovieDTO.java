package com.example.demo.model.dto;

import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {

    String nameMovie;
    Integer durationMovie;
    LocalDateTime startRental;
    LocalDateTime endRental;
    Rating ratingMovie;
    Genre genreMovie;
}
