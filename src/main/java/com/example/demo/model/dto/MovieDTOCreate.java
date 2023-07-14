package com.example.demo.model.dto;


import com.example.demo.model.enums.Genre;
import com.example.demo.model.enums.Rating;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTOCreate {
    String nameMovie;//
    Integer durationMovie;//
    String startRental;
    String endRental;
    Rating ratingMovie;
    Genre genreMovie;
}
