package com.example.demo.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionDTO {

    Long idSession;
    LocalDateTime startSession;
    Integer price;
    MovieDTO movieDTO;
    CinemaDTO cinemaDTO;
    HallDTO hallDTO;

}
