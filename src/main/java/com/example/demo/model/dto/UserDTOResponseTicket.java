package com.example.demo.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTOResponseTicket {

    Long idOrder;

    String startSession;
    Integer price;

    String nameMovie;
    Integer durationMovie;

    String nameCinema;

    Integer numberHall;

    Integer rowNumber;
    Integer placeNumberInRow;

}
