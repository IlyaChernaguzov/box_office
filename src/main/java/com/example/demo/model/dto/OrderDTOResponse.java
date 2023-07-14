package com.example.demo.model.dto;

import com.example.demo.model.enums.Booking;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTOResponse {

    Long idOrder;
    String nameMovie;
    String nameCinema;
    Integer numberHall;
    Integer rowNumber;
    Integer placeNumberInRow;
    String startSession;
    Integer price;
    Booking booking;
    UserDTO userDTO;
}
