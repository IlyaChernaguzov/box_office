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
public class UserDTOResponseSession {

    Long idSession;
    String startSession;
    Integer price;
    String nameMovie;
    Integer durationMovie;
    Rating ratingMovie;
    Genre genreMovie;
    Integer numberHall;
    String nameCinema;
}
