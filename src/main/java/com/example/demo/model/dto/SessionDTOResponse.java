package com.example.demo.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionDTOResponse {

    Long idSession;
    LocalDateTime startSession;
    Integer price;
    MovieDTO movieDTO;
    HallDTOResponse hallDTOResponse;
}
