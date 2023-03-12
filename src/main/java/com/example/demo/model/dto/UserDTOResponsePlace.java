package com.example.demo.model.dto;

import com.example.demo.model.entity.Place;
import com.example.demo.model.enums.Booking;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTOResponsePlace{

    PlaceDTORequest placeDTORequest;
    Booking booking;

}
